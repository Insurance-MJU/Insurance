package controller.web;

import controller.web.dto.ContractResponse;
import domain.ContractList;
import infra.web.Router;
import infra.web.auth.JwtUtil;

import java.util.List;
import java.util.stream.Collectors;

public class ContractController {

    private final ContractList contractList;
    private final JwtUtil jwtUtil;

    public ContractController(ContractList contractList, JwtUtil jwtUtil) {
        this.contractList = contractList;
        this.jwtUtil = jwtUtil;
    }

    public void registerRoutes(Router router) {
        router.get("/contracts",      (req, res) -> res.ok(getAll(req.header("Authorization"))));
        router.get("/contracts/{id}", (req, res) -> res.ok(ContractResponse.from(contractList.getByContractId(req.pathVariable("id")))));
    }

    private List<ContractResponse> getAll(String authHeader) {
        String role   = extract(authHeader, "role");
        String userId = extract(authHeader, "userId");
        ContractList list = ("EMPLOYEE".equals(role) || "ADMIN".equals(role))
                ? contractList.findAll()
                : (userId != null && !userId.isBlank())
                    ? contractList.findByUserId(userId)
                    : contractList.findAll();
        return list.getAll().stream().map(ContractResponse::from).collect(Collectors.toList());
    }

    private String extract(String authHeader, String field) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;
        String token = authHeader.substring(7);
        return "role".equals(field) ? jwtUtil.extractRole(token) : jwtUtil.extractUserId(token);
    }
}
