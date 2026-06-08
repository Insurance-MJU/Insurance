'use client';
import { useEnrollmentStore } from '@/store/enrollmentStore';
import { VEHICLE_PURPOSES } from '@/types/enrollmentConstants';

export default function Step06Usage() {
  const { mileageDiscount, vehiclePurpose, setUsage, nextStep, prevStep } = useEnrollmentStore();

  return (
    <div className="flex flex-col gap-6">
      <div>
        <h2 className="text-xl font-bold">마일리지·운행용도</h2>
        <p className="text-sm text-gray-500 mt-1">운행 목적을 선택하면 맞춤 요율이 적용됩니다.</p>
      </div>

      {/* 마일리지 특약 */}
      <div className="border rounded-xl p-4">
        <div className="flex items-start justify-between">
          <div>
            <p className="font-semibold text-sm">마일리지 특약</p>
            <p className="text-xs text-gray-500 mt-0.5">연간 주행거리 기준 보험료 최대 8% 할인</p>
          </div>
          <div
            onClick={() => setUsage({ mileageDiscount: !mileageDiscount })}
            className={`relative w-11 h-6 rounded-full cursor-pointer transition-colors ${mileageDiscount ? 'bg-blue-500' : 'bg-gray-200'}`}
          >
            <div className={`absolute top-0.5 w-5 h-5 bg-white rounded-full shadow transition-transform ${mileageDiscount ? 'translate-x-5' : 'translate-x-0.5'}`} />
          </div>
        </div>
      </div>

      {/* 운행 용도 */}
      <div>
        <p className="text-sm font-medium mb-2">운행 용도</p>
        <div className="flex flex-col gap-2">
          {VEHICLE_PURPOSES.map((p) => (
            <label
              key={p.value}
              className={`flex items-start gap-3 p-4 border rounded-xl cursor-pointer transition-colors ${
                vehiclePurpose === p.value ? 'border-blue-500 bg-blue-50' : 'border-gray-200'
              }`}
            >
              <input
                type="radio"
                name="purpose"
                value={p.value}
                checked={vehiclePurpose === p.value}
                onChange={() => setUsage({ vehiclePurpose: p.value })}
                className="mt-0.5 accent-blue-500"
              />
              <div>
                <p className="text-sm font-medium">{p.label}</p>
                <p className="text-xs text-gray-500">{p.desc}</p>
              </div>
            </label>
          ))}
        </div>
      </div>

      <div className="flex gap-3">
        <button onClick={prevStep} className="flex-1 border border-gray-300 text-gray-700 font-semibold p-4 rounded-xl">이전</button>
        <button onClick={nextStep} className="flex-1 bg-blue-500 text-white font-semibold p-4 rounded-xl">다음</button>
      </div>
    </div>
  );
}
