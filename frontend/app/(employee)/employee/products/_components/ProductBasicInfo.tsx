export default function ProductBasicInfo({ product }: { product: any }) {
    return (
        <div className="bg-white border border-gray-200 rounded-xl p-5">
            <h2 className="text-sm font-semibold text-gray-600 mb-3">기본 정보</h2>
            <dl className="grid grid-cols-2 gap-x-8 gap-y-2.5 text-sm">
                <div className="flex gap-2">
                    <dt className="text-gray-400 w-24 shrink-0">종목</dt>
                    <dd>{product.lineOfBusinessDisplayName}</dd>
                </div>
                <div className="flex gap-2">
                    <dt className="text-gray-400 w-24 shrink-0">가입대상</dt>
                    <dd>{product.targetCustomer}</dd>
                </div>
                <div className="flex gap-2">
                    <dt className="text-gray-400 w-24 shrink-0">판매시작</dt>
                    <dd>{product.saleStartDate ?? "-"}</dd>
                </div>
                <div className="flex gap-2">
                    <dt className="text-gray-400 w-24 shrink-0">판매종료</dt>
                    <dd>{product.saleEndDate ?? "무기한"}</dd>
                </div>
                {product.description && (
                    <div className="flex gap-2 col-span-2">
                        <dt className="text-gray-400 w-24 shrink-0">설명</dt>
                        <dd>{product.description}</dd>
                    </div>
                )}
            </dl>
        </div>
    );
}
