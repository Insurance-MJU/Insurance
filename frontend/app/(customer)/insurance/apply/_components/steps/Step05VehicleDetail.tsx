'use client';
import { useEnrollmentStore } from '@/store/enrollmentStore';
import { formatMoney } from '@/utils/format';
import { StepHeader } from '@/components/common/ui/StepHeader';
import { StepNavigation } from '@/components/common/ui/StepNavigation';

function Toggle({ label, checked, onChange }: { label: string; checked: boolean; onChange: (v: boolean) => void }) {
  return (
    <label className="flex items-center justify-between p-3 border rounded-lg cursor-pointer">
      <span className="text-sm">{label}</span>
      <div
        onClick={() => onChange(!checked)}
        className={`relative w-11 h-6 rounded-full transition-colors ${checked ? 'bg-blue-500' : 'bg-gray-200'}`}
      >
        <div className={`absolute top-0.5 w-5 h-5 bg-white rounded-full shadow transition-transform ${checked ? 'translate-x-5' : 'translate-x-0.5'}`} />
      </div>
    </label>
  );
}

export default function Step05VehicleDetail() {
  const { vehicleInfo, overrideModelYear, overrideValue, hasBlackbox, hasAdvancedSafety, setVehicleOverride, nextStep, prevStep } =
    useEnrollmentStore();
  const v = vehicleInfo!;
  const displayYear = overrideModelYear ?? v.modelYear;
  const displayValue = overrideValue ?? v.standardValue;

  return (
    <div className="flex flex-col gap-6">
      <StepHeader 
        title="차량 정보 확인" 
        description="차량 정보를 확인하고 필요 시 수정해 주세요." 
      />

      <div className="border rounded-xl divide-y">
        {[
          ['차량번호', v.carNumber],
          ['제조사', v.manufacturer],
          ['모델명', v.modelName],
          ['차종', v.modelType],
          ['연료', v.fuelType],
          ['배기량', `${v.engineCC.toLocaleString()}cc`],
        ].map(([label, value]) => (
          <div key={label} className="flex justify-between items-center px-4 py-3">
            <span className="text-sm text-gray-500">{label}</span>
            <span className="text-sm font-medium">{value}</span>
          </div>
        ))}
      </div>

      <div className="flex flex-col gap-4">
        <div>
          <label className="block text-sm font-medium mb-1">연식</label>
          <input
            type="number"
            min={2000}
            max={2025}
            className="w-full border rounded-lg p-3 text-sm"
            value={displayYear}
            onChange={(e) => setVehicleOverride({ overrideModelYear: parseInt(e.target.value) || v.modelYear })}
          />
        </div>

        <div>
          <label className="block text-sm font-medium mb-1">차량기준가액</label>
          <input
            type="number"
            className="w-full border rounded-lg p-3 text-sm"
            value={displayValue}
            onChange={(e) => setVehicleOverride({ overrideValue: parseInt(e.target.value) || v.standardValue })}
          />
          <p className="text-xs text-gray-400 mt-1">보험개발원 고시 기준: {formatMoney(v.standardValue)}</p>
        </div>

        <div className="flex flex-col gap-2">
          <p className="text-sm font-medium">안전장치</p>
          <Toggle label="블랙박스 장착" checked={hasBlackbox} onChange={(val) => setVehicleOverride({ hasBlackbox: val })} />
          <Toggle label="첨단안전장치 (AEB, LDWS 등)" checked={hasAdvancedSafety} onChange={(val) => setVehicleOverride({ hasAdvancedSafety: val })} />
        </div>
      </div>

      <StepNavigation onPrev={prevStep} onNext={nextStep} />
    </div>
  );
}
