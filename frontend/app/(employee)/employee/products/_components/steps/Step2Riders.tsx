'use client';

interface Props {
  allRiders: any[];
  selRiders: Set<number>;
  toggleRider: (id: number) => void;
}

function RiderBadge({ rider }: { rider: any }) {
  const riderType: string = rider.riderType ?? rider.type ?? "";
  if (riderType === "DISCOUNT") {
    const rate = rider.rate != null ? Math.abs(rider.rate * 100).toFixed(0) : "";
    return (
      <span className="text-xs px-2 py-0.5 rounded-full bg-green-100 text-green-700 font-medium">
        할인특약 {rate ? `-${rate}%` : ""}
      </span>
    );
  }
  if (riderType === "SURCHARGE") {
    const rate = rider.rate != null ? Math.abs(rider.rate * 100).toFixed(0) : "";
    return (
      <span className="text-xs px-2 py-0.5 rounded-full bg-orange-100 text-orange-700 font-medium">
        할증특약 {rate ? `+${rate}%` : ""}
      </span>
    );
  }
  return (
    <span className="text-xs px-2 py-0.5 rounded-full bg-purple-100 text-purple-700 font-medium">
      보장특약
    </span>
  );
}

export function Step2Riders({ allRiders, selRiders, toggleRider }: Props) {
  const selectedCount = selRiders.size;

  if (!allRiders || allRiders.length === 0) {
    return <p className="text-sm text-gray-400 text-center py-8">특약 마스터 데이터가 없습니다.</p>;
  }

  return (
    <div className="space-y-3">
      <div className="flex items-center justify-between bg-blue-50 border border-blue-200 rounded-lg px-4 py-3 mb-4">
        <span className="text-sm text-blue-700 font-medium">선택된 특약</span>
        <span className="text-sm font-bold text-blue-800">{selectedCount}개</span>
      </div>

      {allRiders.map((r: any) => {
        const riderId: number = r.id;
        const isSelected = selRiders.has(riderId);

        return (
          <div
            key={riderId}
            className={`border rounded-lg p-4 transition-colors ${isSelected ? "border-blue-300 bg-blue-50" : "border-gray-200 bg-white"}`}
          >
            <div className="flex items-start gap-2">
              <input
                type="checkbox"
                id={`rider-${riderId}`}
                checked={isSelected}
                onChange={() => toggleRider(riderId)}
                className="w-4 h-4 mt-0.5 accent-blue-600 flex-shrink-0"
              />
              <div className="flex-1 min-w-0">
                <div className="flex items-center gap-2 flex-wrap">
                  <label htmlFor={`rider-${riderId}`} className="text-sm font-medium text-gray-800 cursor-pointer">
                    {r.riderName ?? r.name ?? `특약 ${riderId}`}
                  </label>
                  {r.riderCode && (
                    <span className="text-xs font-mono text-gray-400">{r.riderCode}</span>
                  )}
                  <RiderBadge rider={r} />
                </div>
                {r.description && (
                  <p className="text-xs text-gray-500 mt-1 leading-relaxed">{r.description}</p>
                )}
              </div>
            </div>
          </div>
        );
      })}
    </div>
  );
}
