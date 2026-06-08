export default function ProductRiders({ riders }: { riders: any[] }) {
    if (!riders?.length) return null;
    return (
        <div className="bg-white border border-gray-200 rounded-xl p-5">
            <h2 className="text-sm font-semibold text-gray-600 mb-3">특약 ({riders.length})</h2>
            <div className="flex flex-wrap gap-2">
                {riders.map((r: any) => (
                    <span key={r.id} className="text-xs px-2.5 py-1 bg-blue-50 text-blue-700 rounded-full">
                        {r.riderName}{r.isDefault ? " (기본)" : ""}
                    </span>
                ))}
            </div>
        </div>
    );
}
