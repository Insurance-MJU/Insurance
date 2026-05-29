'use client';

import { SelCoverages } from "@/types/product";

interface Props {
  allCoverages: any[];
  selCoverages: SelCoverages;
  toggleCoverage: (id: number) => void;
  toggleOption: (covId: number, optId: number) => void;
}

function getOptionLabel(opt: any): string {
  if (opt.optionName) return opt.optionName;
  if (!opt.details || opt.details.length === 0) return `옵션 ${opt.id}`;
  const totalDetail = opt.details.find((d: any) => d.detailType === "TOTAL") ?? opt.details[0];
  if (!totalDetail || totalDetail.amount === null) return "무한";
  return `${(totalDetail.amount / 100000000).toFixed(0)}억`;
}

export function Step1Coverages({ allCoverages, selCoverages, toggleCoverage, toggleOption }: Props) {
  if (!allCoverages || allCoverages.length === 0) {
    return <p className="text-sm text-gray-400 text-center py-8">담보 마스터 데이터가 없습니다.</p>;
  }

  return (
    <div className="space-y-3">
      <p className="text-xs text-gray-400 mb-4">
        포함할 담보를 선택하고, 각 담보의 한도 옵션을 선택하세요.
      </p>

      {allCoverages.map((c: any) => {
        const masterId: number = c.id;
        const isSelected = masterId in selCoverages;
        const selectedOpts: Set<number> = isSelected ? selCoverages[masterId] : new Set();
        const hasNoOptions = isSelected && selectedOpts.size === 0;
        const limitOptions: any[] = c.limitOptions ?? [];

        return (
          <div
            key={masterId}
            className={`border rounded-lg p-4 transition-colors ${isSelected ? "border-blue-300 bg-blue-50" : "border-gray-200 bg-white"}`}
          >
            <div className="flex items-center gap-2 mb-2">
              <input
                type="checkbox"
                id={`cov-${masterId}`}
                checked={isSelected}
                onChange={() => toggleCoverage(masterId)}
                className="w-4 h-4 accent-blue-600"
              />
              <label htmlFor={`cov-${masterId}`} className="text-sm font-medium text-gray-800 cursor-pointer">
                {c.coverageName ?? c.name ?? `담보 ${masterId}`}
              </label>
              {c.coverageType && (
                <span className="text-xs px-2 py-0.5 bg-gray-100 text-gray-500 rounded-full">
                  {c.coverageType}
                </span>
              )}
            </div>

            {isSelected && limitOptions.length > 0 && (
              <div className="flex flex-wrap gap-2 mt-3 pl-6">
                {limitOptions.map((opt: any) => {
                  const optId: number = opt.id;
                  const isOptSelected = selectedOpts.has(optId);
                  return (
                    <button
                      key={optId}
                      type="button"
                      onClick={() => toggleOption(masterId, optId)}
                      className={`text-xs px-3 py-1 rounded-full border transition-colors ${
                        isOptSelected
                          ? "bg-blue-600 text-white border-blue-600"
                          : "bg-white text-gray-600 border-gray-300 hover:border-blue-400"
                      }`}
                    >
                      {getOptionLabel(opt)}
                    </button>
                  );
                })}
              </div>
            )}

            {isSelected && limitOptions.length === 0 && (
              <p className="text-xs text-gray-400 pl-6 mt-2">한도 옵션 없음</p>
            )}

            {hasNoOptions && limitOptions.length > 0 && (
              <p className="text-xs text-amber-600 pl-6 mt-2">한도 옵션을 하나 이상 선택하세요.</p>
            )}
          </div>
        );
      })}
    </div>
  );
}
