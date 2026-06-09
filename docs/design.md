# Architecture Design

## 1. Overall Structure

This project uses a plain Java backend without Spring, Gradle, or Maven. The main backend flow is organized as:

```text
controller -> external or domain -> dao -> table
```

Each layer has a fixed responsibility.

| Layer | Package | Responsibility |
|---|---|---|
| Controller | `controller.web`, `controller.cli` | Receives user input or HTTP requests and coordinates use cases. |
| DTO | `controller.web.dto`, `infra.external.*.dto` | Represents input/output data at system boundaries. |
| Domain | `domain` | Owns business state, business rules, and domain object conversion. |
| External | `infra.external` | Wraps outside systems such as identity verification, vehicle inquiry, credit inquiry, bank, FSS, and KIDI. |
| DAO | `infra.dao` | Executes SQL and reads/writes table data through JDBC. |
| VO | `infra.vo` | Carries raw persistence data returned by DAO. |
| Table | `resources/schema.sql` | Stores persistent state in MySQL tables. |

The intended dependency direction is one-way. Controllers call domain objects or external services. Domain collection objects call DAOs. DAOs map database rows into VOs. Tables do not know about application code.

```text
HTTP request
  -> Web Controller
  -> Request DTO
  -> Domain object / Domain List
  -> DAO
  -> VO
  -> DB table

DB table
  -> VO
  -> DAO
  -> Domain List
  -> Domain object
  -> Response DTO
  -> HTTP response
```

## 2. Controller And DTO Design

DTOs are used at the controller boundary. They are not database objects and they are not the main business objects.

### Current Controller And DTO Generation Rules

The current web API is generated around business use cases, not around database tables.

Current counts:

| Type | Count | Location |
|---|---:|---|
| Web controllers | 16 | `src/controller/web` |
| Web boundary DTOs | 18 | `src/controller/web/dto` |
| All request/response DTOs | 42 | `src/dto`, `src/controller/web/dto` |

Controller generation rule:

```text
One controller groups one screen menu or one business workflow.
```

Examples:

| Controller | Generation basis |
|---|---|
| `ProductController` | Product design, approval, rate verification, sale transition, and product documents. |
| `SubscriptionController` | Subscription creation and underwriting review decisions. |
| `ContractController` | Contract list/detail inquiry. |
| `AccidentController` | Accident report, accident search, claim creation, and investigator search. |
| `ClaimController` | Claim assessment and payment state transitions. |
| `RiskAnalysisController` | Underwriting risk analysis for a subscription. |
| `DamageInvestigationController` | Damage investigation registration and inquiry. |
| `AuthController` | Login, signup, and identity login. |
| `VehicleController` | Vehicle inquiry external boundary. |
| `VerificationController` | Identity verification external boundary. |
| `PaymentController` | Payment preparation and confirmation. |
| `BaseRateController` | Base-rate master-data CRUD. |
| `CoverageController` | Coverage master-data CRUD. |
| `ExclusionController` | Exclusion master-data CRUD. |
| `ProvisionController` | Provision master-data and provision-item CRUD. |
| `RiderController` | Rider master-data CRUD. |

DTO generation rule:

```text
Request DTOs are created per frontend input form.
Response DTOs are created per frontend display shape.
```

The number of DTOs is therefore not expected to match the number of domain classes. A DTO is added when the HTTP input/output shape is different from the domain model, when sensitive/internal fields should be hidden, or when one response combines fields from multiple domain objects.

Route generation follows two patterns:

```text
GET/POST /resources
GET /resources/{id}
```

for normal resource lookup and creation, and:

```text
PUT /resources/{id}/business-action
```

for business state transitions such as approval, rejection, assessment, payment, rate verification, and sale confirmation.

The intended controller implementation flow is:

```text
HttpRequest
  -> Request DTO
  -> Domain List or External Service
  -> Domain behavior method
  -> Domain List save
  -> Response DTO
  -> HttpResponse
```

Normal web controllers should not set persistence fields directly and should not expose domain objects directly as API responses.

### Request DTO

Request DTOs represent data received from HTTP request bodies.

Example:

```java
public record SubscriptionCreateRequest(
        String verificationToken,
        String productId,
        String address,
        String carNumber,
        String chassisNumber,
        String occupation,
        String carPurpose,
        String driverScope,
        long premium
) {}
```

`SubscriptionController` receives this DTO through:

```java
req.body(SubscriptionCreateRequest.class)
```

The controller then extracts request values and creates or updates domain objects. The request DTO itself is not passed to the DAO.

For subscription creation, the mapping is:

```text
SubscriptionCreateRequest
  -> SubscriptionController.create()
  -> VerificationService.resolveIdentity()
  -> ProductList.getById()
  -> Subscription.register()
  -> SubscriptionList.save()
```

This keeps HTTP input parsing separate from business behavior.

### Response DTO

Response DTOs represent data returned to the frontend.

Example:

```java
public record SubscriptionResponse(...) {
    public static SubscriptionResponse from(Subscription s) {
        ...
    }
}
```

The controller does not expose the domain object directly. It converts domain objects into response DTOs before sending the response.

```text
Domain object
  -> ResponseDto.from(domain)
  -> res.ok(responseDto)
```

For example:

```java
router.get("/subscriptions/{no}",
        (req, res) -> res.ok(
                SubscriptionResponse.from(
                        subscriptionList.getByNo(req.pathVariable("no"))
                )
        ));
```

This gives the API its own response shape. Internal domain fields can change without forcing the HTTP response contract to match the domain class exactly.

### External DTO

External services have their own DTOs under packages such as:

```text
infra.external.vehicle.dto
infra.external.credit.dto
infra.external.verification.dto
infra.external.bank.dto
```

These DTOs are boundary objects for mock or external integrations. They should be converted into domain concepts before business logic depends on them.

## 3. Domain And DAO Design

The domain layer is the bridge between controller use cases and persistence.

Domain classes such as `Subscription`, `Product`, `Contract`, `Claim`, and `Accident` contain business state and behavior. Domain collection classes such as `SubscriptionList`, `ProductList`, and `ContractList` coordinate DAO access and convert persistence data into domain objects.

Example:

```text
SubscriptionController
  -> SubscriptionList
  -> SubscriptionDao
  -> subscriptions table
```

`SubscriptionList` receives `SubscriptionDao` through its constructor.

```java
public SubscriptionList(SubscriptionDao dao) {
    this.dao = dao;
    this.subscriptions = Collections.emptyList();
}
```

When reading from the database, it asks the DAO for VO objects and converts them into domain objects.

```java
private static Subscription toDomain(SubscriptionVO vo) {
    ...
    Subscription s = Subscription.register(...);
    ...
    return s;
}
```

When saving, it converts a domain object back into a VO and gives that VO to the DAO.

```java
public void save(Subscription s) {
    dao.save(new SubscriptionVO(...));
}
```

This design keeps SQL and table-shaped data out of controllers. It also keeps business methods out of DAO classes.

## 4. DAO And VO Design

VOs are persistence value objects. In this project, they are placed under:

```text
src/infra/vo
```

A VO represents data that is close to a database row or a joined persistence result.

Example:

```java
public class SubscriptionVO {
    public final String subscriptionNo;
    public final String userId;
    public final String applicantName;
    ...
}
```

DAOs return VOs, not domain objects.

Example:

```java
public SubscriptionVO findByNo(String subscriptionNo) {
    return db.queryForObject(
            "SELECT * FROM subscriptions WHERE subscription_no = ?",
            EXTRACTOR,
            subscriptionNo
    );
}
```

DAO row mapping is done through `ResultSetExtractor`.

```java
private static final ResultSetExtractor<SubscriptionVO> EXTRACTOR = rs -> mapRow(rs);
```

The mapping flow is:

```text
ResultSet
  -> DAO mapRow()
  -> VO
  -> Domain List toDomain()
  -> Domain object
```

The reverse save flow is:

```text
Domain object
  -> Domain List save()
  -> VO
  -> DAO save()
  -> SQL INSERT/UPDATE
  -> table
```

VOs are intentionally simple. They do not contain business methods. Their job is to move persistence data between DAO and domain conversion code.

## 5. External Service Flow

External integrations are accessed from controllers or domain use-case coordination code when the use case needs outside information.

Examples:

| External service | Package | Example responsibility |
|---|---|---|
| Verification | `infra.external.verification` | Resolve identity from verification token. |
| Vehicle | `infra.external.vehicle` | Vehicle inquiry. |
| Credit | `infra.external.credit` | Credit information inquiry. |
| Bank | `infra.external.bank` | Account verification or transfer mock. |
| FSS | `infra.external.fss` | Financial authority mock integration. |
| KIDI | `infra.external.kidi` | Insurance development institute mock integration. |

For subscription creation, the controller first calls `VerificationService` to resolve identity, then builds the domain object.

```text
SubscriptionController
  -> VerificationService
  -> VerifiedIdentity
  -> Subscription.register()
```

External DTOs should not be stored directly in tables and should not replace domain objects.

## 6. Current Exceptions

Most web controllers follow the intended structure and call domain list objects rather than DAOs directly.

There are current exceptions:

| File | Exception |
|---|---|
| `controller.cli.LoginController` | Directly uses `UserDao` and `UserVO` for CLI login. |

This is accepted as a current implementation exception. Master-data web controllers now follow the normal dependency direction through domain list classes such as `BaseRateList`, `CoverageList`, `ExclusionList`, `ProvisionList`, and `RiderList`.

For new code, the preferred rule is:

```text
Controller should not directly depend on infra.dao or infra.vo.
```

If similar functionality is added later, prefer introducing a domain list or application-level coordinator instead of adding more direct DAO access to controllers.

## 7. AI Coding Control Rules

When using AI to modify or generate code in this project, the following rules should be given explicitly.

1. Keep the dependency direction as `controller -> external or domain -> dao -> table`.
2. Put HTTP request and response shapes in `controller.web.dto`.
3. Do not pass controller request DTOs into DAO methods.
4. Do not return VO objects from normal web controller responses.
5. DAO methods should return VO, `List<VO>`, primitive values, or simple persistence results.
6. Domain list classes should convert VO objects into domain objects.
7. Response DTOs should be created from domain objects using methods such as `from(domain)`.
8. New controller code should not import `infra.dao` or `infra.vo` unless it is a documented exception.
9. VO classes should remain simple data carriers and should not contain business logic.
10. Business state transitions should stay in domain classes, not in controllers or DAOs.

These rules make AI-generated changes easier to review because each generated class has a clear layer and a clear dependency boundary.

## 8. Architecture Check Script

Architecture dependency rules can be checked with:

```powershell
powershell -ExecutionPolicy Bypass -File scripts\check-architecture.ps1
```

The script checks Java imports against layer rules. The current rules are defined at the top of:

```text
scripts/check-architecture.ps1
```

To add or change a rule, edit the `$Rules` array. Each rule supports:

| Field | Meaning |
|---|---|
| `Name` | Rule name shown in failure output. |
| `Include` | Relative file path patterns to check. |
| `Exclude` | Relative file path patterns to skip. |
| `AllowedImports` | Import prefixes that are allowed. |
| `DeniedImports` | Import prefixes that always fail. |
| `Description` | Human-readable explanation. |

Current checks:

1. Controllers must not import `infra.dao` or `infra.vo`, except documented files.
2. Web controllers may depend on web DTOs, domain, external services, web infra, config, common, and JDK APIs.
3. CLI controllers may depend on CLI context, domain, external services, common, and JDK APIs.
4. Domain may depend on domain/common/JDK code and the current persistence boundary types `infra.dao`, `infra.vo`, and `dto`.
5. DAO may depend only on persistence helpers, VO, common, and JDK APIs.

## 9. Domain Class Diagram

The current domain model diagram is maintained separately:

```text
docs/domain-class-diagram.md
```

The diagram is based on the current Java files under `src/domain` and focuses on the main aggregate, value-object, enum, and domain-list relationships.
