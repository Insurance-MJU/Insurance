package controller.web;

import controller.web.dto.AccidentReportRequest;
import controller.web.dto.AccidentResponse;
import controller.web.dto.ClaimAssignRequest;
import controller.web.dto.ClaimResponse;
import domain.*;
import infra.web.Router;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class AccidentController {

    private final AccidentList accidentList;
    private final ClaimList claimList;
    private final ContractList contractList;
    private final FieldInvestigatorList fieldInvestigatorList;

    public AccidentController(AccidentList accidentList, ClaimList claimList,
                              ContractList contractList, FieldInvestigatorList fieldInvestigatorList) {
        this.accidentList = accidentList;
        this.claimList = claimList;
        this.contractList = contractList;
        this.fieldInvestigatorList = fieldInvestigatorList;
    }

    public void registerRoutes(Router router) {
        router.get("/accidents",             (req, res) -> res.ok(search(req.queryParam("date"), req.queryParam("status"))));
        router.get("/accidents/{id}",        (req, res) -> res.ok(AccidentResponse.from(accidentList.getById(req.pathVariable("id")))));
        router.post("/accidents",            (req, res) -> res.created(report(req.body(AccidentReportRequest.class))));
        router.put("/accidents/{id}/assign", (req, res) -> res.created(assign(req.pathVariable("id"), req.body(ClaimAssignRequest.class))));
    }

    private List<AccidentResponse> search(String date, String status) {
        return accidentList.findByDateAndStatus(
                date   != null ? date   : "",
                status != null ? status : ""
        ).getAll().stream().map(AccidentResponse::from).collect(Collectors.toList());
    }

    private AccidentResponse report(AccidentReportRequest req) {
        Contract contract = contractList.getByContractId(req.contractId());
        Accident accident = Accident.report(
                accidentList.nextId(),
                req.reportedBy(), req.phone(),
                req.accidentDate(), req.accidentLocation(),
                req.accidentDetail(), req.documents(),
                contract
        );
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
        accident.setStatus(AccidentStatus.IN_PROGRESS);
        accidentList.save(accident);
        return ClaimResponse.from(claim);
    }
}
