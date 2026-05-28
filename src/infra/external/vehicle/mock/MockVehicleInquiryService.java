package infra.external.vehicle.mock;

import infra.external.vehicle.VehicleInquiryService;
import infra.external.vehicle.dto.VehicleInquiryRequest;
import infra.external.vehicle.dto.VehicleInquiryResponse;

import java.time.LocalDateTime;

/**
 * 차량정보 조회 Mock 구현체
 * 번호판 마지막 숫자로 결정론적 차량 데이터 생성
 * 실제로는 보험개발원 API → 차량등록원부 조회
 */
public class MockVehicleInquiryService implements VehicleInquiryService {

    private static final String[][] MODELS = {
        // { manufacturer, modelName, modelType, engineCC, fuelType }
        { "현대", "캐스퍼",   "소형A", "1000", "GASOLINE" },
        { "기아", "모닝",     "소형A", "1000", "GASOLINE" },
        { "현대", "아반떼",   "소형B", "1600", "GASOLINE" },
        { "기아", "K3",       "소형B", "1600", "GASOLINE" },
        { "현대", "쏘나타",   "중형",  "2000", "GASOLINE" },
        { "기아", "K5",       "중형",  "2000", "HYBRID"   },
        { "현대", "그랜저",   "대형",  "3000", "GASOLINE" },
        { "기아", "K8",       "대형",  "3000", "HYBRID"   },
        { "현대", "스타리아", "다인승", "2200", "DIESEL"  },
        { "BMW",  "5시리즈",  "대형",  "2000", "GASOLINE" },
    };

    private static final long[] BASE_VALUES = {
        12_000_000L, 15_000_000L, 23_000_000L, 25_000_000L,
        32_000_000L, 35_000_000L, 48_000_000L, 52_000_000L,
        38_000_000L, 75_000_000L,
    };

    @Override
    public VehicleInquiryResponse inquire(VehicleInquiryRequest request) {
        String carNumber = request.carNumber();
        if (carNumber == null || carNumber.isBlank())
            return failure(carNumber, "번호판이 비어 있습니다.");

        int seed      = extractSeed(carNumber);
        String[] model = MODELS[seed];
        int modelYear  = 2020 + (seed % 6);

        boolean hasABS      = modelYear >= 2021 || seed % 3 != 0;
        int     airbagCount = modelYear >= 2022 ? 6 : 4;
        boolean hasBlackbox = seed % 4 != 0;

        int yearsOld = 2025 - modelYear;
        long standardValue = (long)(BASE_VALUES[seed] * Math.pow(0.92, yearsOld));

        return new VehicleInquiryResponse(
                carNumber,
                model[0], model[1], model[2],
                modelYear,
                Integer.parseInt(model[3]),
                model[4],
                standardValue,
                hasABS, airbagCount, hasBlackbox,
                LocalDateTime.now().toString(),
                null
        );
    }

    private int extractSeed(String carNumber) {
        String digits = carNumber.replaceAll("[^0-9]", "");
        if (digits.isEmpty()) return 0;
        return Integer.parseInt(String.valueOf(digits.charAt(digits.length() - 1))) % MODELS.length;
    }

    private VehicleInquiryResponse failure(String carNumber, String reason) {
        return new VehicleInquiryResponse(
                carNumber, null, null, null,
                0, 0, null, 0,
                false, 0, false,
                LocalDateTime.now().toString(),
                reason
        );
    }
}
