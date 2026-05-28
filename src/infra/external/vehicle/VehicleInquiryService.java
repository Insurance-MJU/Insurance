package infra.external.vehicle;

import infra.external.vehicle.dto.VehicleInquiryRequest;
import infra.external.vehicle.dto.VehicleInquiryResponse;

/**
 * 차량정보 조회 외부 서비스 인터페이스
 * 실제 구현체는 보험개발원 차량기준가액 API 또는 공공데이터 포털과 연동
 */
public interface VehicleInquiryService {
    VehicleInquiryResponse inquire(VehicleInquiryRequest request);
}
