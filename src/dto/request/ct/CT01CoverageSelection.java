package dto.request.ct;

import java.util.List;

/**
 * CT-01 담보 선택 항목 (CT01ProductDesignRequest 내부에서 사용)
 */
public record CT01CoverageSelection(
    String coverageId,
    List<String> optionIds  // 선택한 가입 옵션 ID 목록
) {}
