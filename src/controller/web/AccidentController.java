package controller.web;

import controller.web.dto.AccidentReportRequest;
import controller.web.dto.AccidentResponse;
import controller.web.dto.ClaimAssignRequest;
import controller.web.dto.ClaimResponse;
import controller.web.dto.InvestigatorResponse;
import domain.*;
import infra.web.Router;
import infra.web.auth.JwtUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class AccidentController {

    private final AccidentList accidentList;
    private final ClaimList claimList;
    private final ContractList contractList;
    private final FieldInvestigatorList fieldInvestigatorList;
    private final JwtUtil jwtUtil;

    public AccidentController(AccidentList accidentList, ClaimList claimList,
                              ContractList contractList, FieldInvestigatorList fieldInvestigatorList,
                              JwtUtil jwtUtil) {
        this.accidentList = accidentList;
        this.claimList = claimList;
        this.contractList = contractList;
        this.fieldInvestigatorList = fieldInvestigatorList;
        this.jwtUtil = jwtUtil;
    }

    public void registerRoutes(Router router) {
        router.get("/accidents",             (req, res) -> res.ok(search(req.queryParam("date"), req.queryParam("status"), req.header("Authorization"))));
        router.get("/accidents/{id}",        (req, res) -> res.ok(AccidentResponse.from(accidentList.getById(req.pathVariable("id")))));
        router.get("/accidents/{id}/claim",  (req, res) -> res.ok(getClaimByAccident(req.pathVariable("id"))));
        router.post("/accidents",            (req, res) -> res.created(report(req.body(AccidentReportRequest.class), req.header("Authorization"))));
        router.put("/accidents/{id}/assign", (req, res) -> res.created(assign(req.pathVariable("id"), req.body(ClaimAssignRequest.class))));
        router.get("/investigators",         (req, res) -> res.ok(searchInvestigators(req.queryParam("specialty"))));
    }

    private List<InvestigatorResponse> searchInvestigators(String specialty) {
        return fieldInvestigatorList.findBySpecialty(specialty != null ? specialty : "")
                .getAll().stream()
                .map(InvestigatorResponse::from)
                .collect(Collectors.toList());
    }

    private String extractUserId(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;
        return jwtUtil.extractUserId(authHeader.substring(7));
    }

    private String extractRole(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;
        return jwtUtil.extractRole(authHeader.substring(7));
    }

    private List<AccidentResponse> search(String date, String status, String authHeader) {
        String role = extractRole(authHeader);
        // 직원/관리자는 date/status 필터로 전체 조회
        if ("EMPLOYEE".equals(role) || "ADMIN".equals(role)) {
            return accidentList.findByDateAndStatus(
                    date   != null ? date   : "",
                    status != null ? status : ""
            ).getAll().stream().map(AccidentResponse::from).collect(Collectors.toList());
        }
        // 고객은 본인 userId 기반 조회
        String userId = extractUserId(authHeader);
        if (userId != null) {
            return accidentList.findByUserId(userId)
                    .getAll().stream().map(AccidentResponse::from).collect(Collectors.toList());
        }
        return accidentList.findByDateAndStatus(
                date   != null ? date   : "",
                status != null ? status : ""
        ).getAll().stream().map(AccidentResponse::from).collect(Collectors.toList());
    }

    private Object getClaimByAccident(String accidentId) {
        domain.Claim claim = claimList.findByAccidentId(accidentId);
        if (claim == null) return java.util.Map.of("exists", false);
        return controller.web.dto.ClaimResponse.from(claim);
    }

    private AccidentResponse report(AccidentReportRequest req, String authHeader) {
        Contract contract = contractList.getByContractId(req.contractId());
        Accident accident = Accident.report(
                accidentList.nextId(),
                req.reportedBy(), req.phone(),
                req.accidentDate(), req.accidentLocation(),
                req.accidentDetail(), req.documents(),
                contract
        );
        accident.setUserId(extractUserId(authHeader));
        accidentList.save(accident);
        return AccidentResponse.from(accident);
    }

    private ClaimResponse assign(String accidentId, ClaimAssignRequest req) {
        Accident accident = accidentList.getById(accidentId);
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        Claim claim = new Claim(
                claimList.nextId(), accident,
                accident.getReportedBy(), now,
                accident.getContractId(),
                accident.getDescription(), ClaimStatus.INVESTIGATING
        );
        claim.setAssignedEmployee(req.employeeId());
        claimList.save(claim);
        accident.setStatus(AccidentStatus.INVESTIGATING);
        accidentList.save(accident);
        return ClaimResponse.from(claim);
    }
}
