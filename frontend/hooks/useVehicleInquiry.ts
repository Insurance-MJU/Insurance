import { useState } from 'react';
import { useEnrollmentStore } from '@/store/enrollmentStore';
import { fetchApi } from '@/queries/api';

export function useVehicleInquiry() {
  const { carNumber, setVehicleInfo, nextStep } = useEnrollmentStore();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleInquire = async () => {
    const carNo = carNumber.trim().replace(/\s/g, '');
    if (!carNo) return;
    setLoading(true);
    setError('');
    try {
      const data = await fetchApi(`/vehicles/${encodeURIComponent(carNo)}`);
      const vehicle = data.data ?? data;
      if (vehicle.failureReason) {
        setError(vehicle.failureReason);
      } else {
        setVehicleInfo(vehicle);
        nextStep();
      }
    } catch (e: any) {
      if (e?.status === 404) {
        setError('차량 정보를 찾을 수 없습니다. 차량번호를 확인해 주세요.');
      } else {
        setError('차량 조회에 실패했습니다. 다시 시도해 주세요.');
      }
    } finally {
      setLoading(false);
    }
  };

  return { handleInquire, loading, error, setError };
}
