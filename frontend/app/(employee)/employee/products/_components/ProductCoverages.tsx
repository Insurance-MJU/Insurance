export default function ProductCoverages({ coverages }: { coverages: any[] }) {
    if (!coverages?.length) return null;
    return (
        <div className="bg-white border border-gray-200 rounded-xl p-5">
            <h2 className="text-sm font-semibold text-gray-600 mb-3">담보 ({coverages.length})</h2>
            <table className="w-full text-xs">
                <thead>
                    <tr className="text-gray-400 border-b border-gray-100">
                        <th className="pb-2 text-left font-medium">담보명</th>
                        <th className="pb-2 text-left font-medium">유형</th>
                        <th className="pb-2 text-right font-medium">기준보험료</th>
                        <th className="pb-2 text-center font-medium">필수</th>
                    </tr>
                </thead>
                <tbody className="divide-y divide-gray-50">
                    {coverages.map((c: any) => (
                        <tr key={c.id}>
                            <td className="py-2">{c.coverageName}</td>
                            <td className="py-2 text-gray-400">{c.coverageType}</td>
                            <td className="py-2 text-right">{c.basePremium?.toLocaleString()}원</td>
                            <td className="py-2 text-center">{c.mandatory ? "✓" : ""}</td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
}
