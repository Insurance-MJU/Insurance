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
        // CL-01: 사고 목록 조회 (date, status 쿼리 파라미터)
        router.get("/accidents", (req, res) -> {
            String date   = req.queryParam("date");
            String status = req.queryParam("status");
            AccidentList results = accidentList.findByDateAndStatus(
                    date   != null ? date   : "",
                    status != null ? status : ""
            );
            List<AccidentResponse> body = results.getAll().stream()
                    .map(AccidentResponse::from)
                    .collect(Collectors.toList());
            res.ok(body);
        });

        // CL-01: 사고 상세 조회
        router.get("/accidents/{id}", (req, res) -> {
            Accident a = accidentList.findById(req.pathVariable("id"));
            if (a == null) { res.error(404, "사고를 찾을 수 없습니다."); return; }
            res.ok(AccidentResponse.from(a));
        });

        // CS-04: 고객 사고 접수 (보험금 청구)
        router.post("/accidents", (req, res) -> {
            AccidentReportRequest body = req.body(AccidentReportRequest.class);
            Contract contract = contractList.findById(body.contractId());
            if (contract == null) { res.error(404, "계약을 찾을 수 없습니다."); return; }

            String accidentId = accidentList.nextId();
            Accident accident = Accident.report(
                    accidentId,
                    body.reportedBy(), body.phone(),
                    body.accidentDate(), body.accidentLocation(),
                    body.accidentDetail(), body.documents(),
                    contract
            );
            accidentList.save(accident);
            res.created(AccidentResponse.from(accident));
        });

        // CL-01: 담당자 배당 및 클레임 생성
        router.put("/accidents/{id}/assign", (req, res) -> {
            Accident accident = accidentList.findById(req.pathVariable("id"));
            if (accident == null) { res.error(404, "사고를 찾을 수 없습니다."); return; }

            ClaimAssignRequest body = req.body(ClaimAssignRequest.class);
            String claimId = claimList.nextId();
            String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            Claim claim = new Claim(
                    claimId, accident,
                    accident.getReportedBy(), now,
                    accident.getContractId(),
                    accident.getDescription(), ClaimStatus.INVESTIGATING
            );
            claim.setAssignedEmployee(body.employeeId());
            claimList.save(claim);

            accident.setStatus(AccidentStatus.IN_PROGRESS);
            accidentList.save(accident);

            res.created(ClaimResponse.from(claim));
        });
    }
}
