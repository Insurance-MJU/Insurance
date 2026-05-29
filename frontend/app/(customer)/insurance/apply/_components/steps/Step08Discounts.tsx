'use client';
import { useEnrollmentStore } from '@/store/enrollmentStore';
import { useOnSaleProduct } from '@/queries/products';
import type { ProductRider } from '@/types/product';
import type { SelectedAdjustment } from '@/types/enrollment';

export default function Step08Discounts() {
  const { productId, selectedAdjustments, toggleAdjustment, nextStep, prevStep } = useEnrollmentStore();
  const { data: product } = useOnSaleProduct(productId);

  const discountRiders: ProductRider[] = (product?.riders ?? [])
    .filter((r) => r.riderType === 'DISCOUNT')
    .sort((a, b) => a.sortOrder - b.sortOrder);

  const isSelected = (riderName: string) =>
    selectedAdjustments.some((a) => a.itemName === riderName);

  const handleToggle = (rider: ProductRider) => {
    const selected: SelectedAdjustment = {
      itemName: rider.riderName,
      adjType: 'DISCOUNT',
      rate: rider.discountRate ?? 0,
    };
    toggleAdjustment(selected);
  };

  return (
    <div className="flex flex-col gap-6">
      <div>
        <h2 className="text-xl font-bold">할인 특약 선택</h2>
        <p className="text-sm text-gray-500 mt-1">해당하는 특약을 모두 선택하면 보험료가 추가로 할인됩니다.</p>
      </div>

      <div className="flex flex-col gap-3">
        {discountRiders.length === 0 ? (
          <p className="text-gray-400 text-sm py-4">적용 가능한 할인 특약이 없습니다.</p>
        ) : (
          discountRiders.map((rider) => {
            const selected = isSelected(rider.riderName);
            return (
              <div
                key={rider.id}
                onClick={() => handleToggle(rider)}
                className={`flex items-start justify-between p-4 border rounded-xl cursor-pointer transition-colors ${
                  selected ? 'border-blue-500 bg-blue-50' : 'border-gray-200'
                }`}
              >
                <div className="flex-1 pr-3">
                  <div className="flex items-center gap-2">
                    <p className="text-sm font-medium">{rider.riderName}</p>
                    {rider.discountRate != null && (
                      <span className="text-xs text-green-600 font-semibold">
                        {(rider.discountRate * 100).toFixed(1)}% 할인
                      </span>
                    )}
                  </div>
                  {rider.description && (
                    <p className="text-xs text-gray-500 mt-0.5">{rider.description}</p>
                  )}
                </div>
                <div
                  className={`relative w-11 h-6 rounded-full flex-shrink-0 mt-0.5 transition-colors ${
                    selected ? 'bg-blue-500' : 'bg-gray-200'
                  }`}
                >
                  <div className={`absolute top-0.5 w-5 h-5 bg-white rounded-full shadow transition-transform ${
                    selected ? 'translate-x-5' : 'translate-x-0.5'
                  }`} />
                </div>
              </div>
            );
          })
        )}
      </div>

      <div className="flex gap-3">
        <button onClick={prevStep} className="flex-1 border border-gray-300 text-gray-700 font-semibold p-4 rounded-xl">이전</button>
        <button onClick={nextStep} className="flex-1 bg-blue-500 text-white font-semibold p-4 rounded-xl">보험료 계산</button>
      </div>
    </div>
  );
}
