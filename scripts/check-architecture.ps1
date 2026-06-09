param(
    [string] $Root = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path,
    [switch] $ListRules
)

$ErrorActionPreference = "Stop"

# Add or change rules here.
#
# Include / Exclude are path patterns relative to the repository root.
# The pattern matcher is intentionally simple:
#   - use forward slashes
#   - use * as "any text", including directory separators
#
# AllowedImports means every non-java import in matching files must start with
# one of these prefixes. DeniedImports means matching imports always fail.
# DeniedImports are checked first.
$Rules = @(
    @{
        Name = "controller-must-not-use-dao-or-vo"
        Include = @("src/controller/*.java")
        Exclude = @(
            "src/controller/cli/LoginController.java"
        )
        AllowedImports = $null
        DeniedImports = @("infra.dao", "infra.vo")
        Description = "Controllers may call domain, dto, external, web infra, and common code, but not DAO or VO directly."
    },
    @{
        Name = "web-controller-allowed-dependencies"
        Include = @("src/controller/web/*.java")
        Exclude = @()
        AllowedImports = @(
            "controller.web",
            "domain",
            "infra.external",
            "infra.web",
            "infra.config",
            "common",
            "java",
            "javax"
        )
        DeniedImports = @("infra.dao", "infra.vo")
        Description = "Web controllers should depend only on web DTOs, domain, external services, web infra, config, common, and JDK APIs."
    },
    @{
        Name = "cli-controller-allowed-dependencies"
        Include = @("src/controller/cli/*.java")
        Exclude = @(
            "src/controller/cli/LoginController.java"
        )
        AllowedImports = @(
            "controller.cli",
            "domain",
            "infra.external",
            "common",
            "java",
            "javax"
        )
        DeniedImports = @("infra.dao", "infra.vo")
        Description = "CLI controllers should depend on CLI context, domain, external services, common, and JDK APIs."
    },
    @{
        Name = "domain-allowed-dependencies"
        Include = @("src/domain/*.java")
        Exclude = @()
        AllowedImports = @(
            "domain",
            "common",
            "infra.dao",
            "infra.vo",
            "dto",
            "java",
            "javax"
        )
        DeniedImports = @(
            "controller",
            "infra.web",
            "infra.external",
            "infra.persistence",
            "infra.config"
        )
        Description = "Domain may use domain/common/JDK code and the persistence boundary types currently used by domain lists."
    },
    @{
        Name = "dao-allowed-dependencies"
        Include = @("src/infra/dao/*.java")
        Exclude = @()
        AllowedImports = @(
            "infra.dao",
            "infra.persistence",
            "infra.vo",
            "common",
            "java",
            "javax"
        )
        DeniedImports = @(
            "controller",
            "domain",
            "dto",
            "infra.external",
            "infra.web",
            "infra.config"
        )
        Description = "DAO classes should depend only on DB/persistence helpers, VO, common exceptions, and JDK APIs."
    }
)

function Convert-ToRelativePath {
    param(
        [Parameter(Mandatory = $true)] [string] $BasePath,
        [Parameter(Mandatory = $true)] [string] $Path
    )

    $baseUri = [System.Uri]((Resolve-Path $BasePath).Path.TrimEnd("\") + "\")
    $pathUri = [System.Uri]((Resolve-Path $Path).Path)
    return $baseUri.MakeRelativeUri($pathUri).ToString() -replace "\\", "/"
}

function Convert-PatternToRegex {
    param([Parameter(Mandatory = $true)] [string] $Pattern)

    $normalized = $Pattern -replace "\\", "/"
    $escaped = [regex]::Escape($normalized)
    $regex = $escaped -replace "\\\*", ".*"
    return "^$regex$"
}

function Test-PathPattern {
    param(
        [Parameter(Mandatory = $true)] [string] $RelativePath,
        [Parameter(Mandatory = $true)] [string[]] $Patterns
    )

    foreach ($pattern in $Patterns) {
        if ($RelativePath -match (Convert-PatternToRegex $pattern)) {
            return $true
        }
    }
    return $false
}

function Test-ImportPrefix {
    param(
        [Parameter(Mandatory = $true)] [string] $ImportName,
        [Parameter(Mandatory = $true)] [string[]] $Prefixes
    )

    foreach ($prefix in $Prefixes) {
        if ($ImportName -eq $prefix -or $ImportName.StartsWith("$prefix.")) {
            return $true
        }
    }
    return $false
}

function Get-JavaImports {
    param([Parameter(Mandatory = $true)] [string] $Path)

    $result = @()
    $lineNo = 0
    foreach ($line in Get-Content -LiteralPath $Path) {
        $lineNo++
        if ($line -match "^\s*import\s+(static\s+)?([^;]+);") {
            $result += [pscustomobject]@{
                Line = $lineNo
                Name = $Matches[2].Trim()
                Code = $line.Trim()
            }
        }
    }
    return $result
}

if ($ListRules) {
    foreach ($rule in $Rules) {
        Write-Host "[$($rule.Name)]"
        Write-Host "  Include: $($rule.Include -join ', ')"
        if ($rule.Exclude.Count -gt 0) {
            Write-Host "  Exclude: $($rule.Exclude -join ', ')"
        }
        if ($rule.AllowedImports) {
            Write-Host "  AllowedImports: $($rule.AllowedImports -join ', ')"
        }
        if ($rule.DeniedImports.Count -gt 0) {
            Write-Host "  DeniedImports: $($rule.DeniedImports -join ', ')"
        }
        Write-Host "  $($rule.Description)"
        Write-Host ""
    }
    exit 0
}

$srcPath = Join-Path $Root "src"
if (-not (Test-Path -LiteralPath $srcPath)) {
    Write-Error "Source directory not found: $srcPath"
    exit 2
}

$javaFiles = Get-ChildItem -LiteralPath $srcPath -Recurse -Filter "*.java" -File
$violations = @()
$ruleStats = @{}

foreach ($rule in $Rules) {
    $ruleStats[$rule.Name] = [ordered]@{
        Files = 0
        Imports = 0
        Violations = 0
    }
}

Write-Host "Architecture check started."
Write-Host "Root: $Root"
Write-Host "Java files: $($javaFiles.Count)"
Write-Host ""

foreach ($file in $javaFiles) {
    $relativePath = Convert-ToRelativePath -BasePath $Root -Path $file.FullName
    $imports = Get-JavaImports -Path $file.FullName

    foreach ($rule in $Rules) {
        if (-not (Test-PathPattern -RelativePath $relativePath -Patterns $rule.Include)) {
            continue
        }
        if ($rule.Exclude.Count -gt 0 -and (Test-PathPattern -RelativePath $relativePath -Patterns $rule.Exclude)) {
            continue
        }

        $ruleStats[$rule.Name].Files++
        $ruleStats[$rule.Name].Imports += $imports.Count

        foreach ($import in $imports) {
            if ($rule.DeniedImports.Count -gt 0 -and (Test-ImportPrefix -ImportName $import.Name -Prefixes $rule.DeniedImports)) {
                $violations += [pscustomobject]@{
                    Rule = $rule.Name
                    File = $relativePath
                    Line = $import.Line
                    Import = $import.Name
                    Code = $import.Code
                    Reason = "Denied import prefix"
                }
                $ruleStats[$rule.Name].Violations++
                continue
            }

            if ($rule.AllowedImports -and -not (Test-ImportPrefix -ImportName $import.Name -Prefixes $rule.AllowedImports)) {
                $violations += [pscustomobject]@{
                    Rule = $rule.Name
                    File = $relativePath
                    Line = $import.Line
                    Import = $import.Name
                    Code = $import.Code
                    Reason = "Import is not in allowed prefixes"
                }
                $ruleStats[$rule.Name].Violations++
            }
        }
    }
}

$stepNo = 0
foreach ($rule in $Rules) {
    $stepNo++
    $stats = $ruleStats[$rule.Name]
    $label = "[$stepNo/$($Rules.Count)] $($rule.Name)"
    if ($stats.Violations -eq 0) {
        Write-Host "$label completed - files: $($stats.Files), imports: $($stats.Imports), violations: 0" -ForegroundColor Green
    } else {
        Write-Host "$label failed - files: $($stats.Files), imports: $($stats.Imports), violations: $($stats.Violations)" -ForegroundColor Red
    }
}
Write-Host ""

if ($violations.Count -eq 0) {
    Write-Host "Architecture check passed. Checked $($javaFiles.Count) Java files."
    exit 0
}

Write-Host "Architecture check failed with $($violations.Count) violation(s)." -ForegroundColor Red
Write-Host ""

foreach ($group in ($violations | Sort-Object Rule, File, Line | Group-Object Rule)) {
    Write-Host "Rule: $($group.Name)" -ForegroundColor Yellow
    foreach ($violation in $group.Group) {
        Write-Host "  $($violation.File):$($violation.Line)"
        Write-Host "    Import: $($violation.Import)"
        Write-Host "    Reason: $($violation.Reason)"
        Write-Host "    Code:   $($violation.Code)"
    }
    Write-Host ""
}

exit 1
