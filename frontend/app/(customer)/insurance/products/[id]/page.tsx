'use client';
import Link from 'next/link';
import { useParams, useRouter } from 'next/navigation';
import { useOnSaleProduct } from '@/queries/products';
import type { ProductRider } from '@/types/product';

export default function ProductDetailPage() {
  const params = useParams();
  const router = useRouter();
  const id = Number(params.id);
  const { data: product, isLoading, error } = useOnSaleProduct(id);

  if (isLoading) {
    return (
      <main className="max-w-xl mx-auto p-4">
        <div className="animate-pulse space-y-4">
          <div className="h-6 bg-gray-200 rounded w-1/3" />
          <div className="h-8 bg-gray-200 rounded w-2/3" />
          <div className="h-4 bg-gray-100 rounded w-1/2" />
          <div className="h-32 bg-gray-100 rounded" />
          <div className="h-32 bg-gray-100 rounded" />
        </div>
      </main>
    );
  }

  if (error || !product) {
    return (
      <main className="max-w-xl mx-auto p-4">
        <p className="text-gray-400 text-center py-16">상품 정보를 불러올 수 없습니다.</p>
        <button onClick={() => router.back()} className="w-full mt-4 py-3 border rounded-lg text-sm">
          돌아가기
        </button>
      </main>
    );
  }

  return (
    <main className="max-w-xl mx-auto p-4 pb-28">
      {/* 헤더 */}
      <button onClick={() => router.back()} className="text-sm text-gray-500 mb-4">← 목록으로</button>
      <div className="flex items-start justify-between mb-1">
        <h1 className="text-2xl font-bold">{product.productName}</h1>
        <span className="text-xs bg-green-100 text-green-700 px-2 py-1 rounded-full font-medium mt-1">
          판매 중
        </span>
      </div>
      <p className="text-sm text-gray-400 mb-1">{product.lineOfBusinessDisplayName}</p>
      {product.targetCustomer && (
        <p className="text-xs text-gray-400 mb-4">대상: {product.targetCustomer}</p>
      )}
      {product.description && (
        <p className="text-gray-600 text-sm mb-6 leading-relaxed">{product.description}</p>
      )}

      {/* 판매 기간 */}
      {(product.saleStartDate || product.saleEndDate) && (
        <div className="bg-gray-50 rounded-lg p-3 mb-6 text-xs text-gray-500">
          판매 기간: {product.saleStartDate ?? '—'} ~ {product.saleEndDate ?? '계속'}
        </div>
      )}

      {/* 특약 */}
      {product.riders.length > 0 && (
        <Section title="특약">
          <div className="divide-y">
            {product.riders.map((r) => (
              <RiderRow key={r.id} rider={r} />
            ))}
          </div>
        </Section>
      )}

      {/* 하단 고정 버튼 */}
      <div className="fixed bottom-0 left-0 right-0 bg-white border-t p-4 max-w-xl mx-auto">
        <Link
          href={`/insurance/apply?prodId=${product.id}`}
          className="block w-full bg-blue-500 hover:bg-blue-600 text-white text-center py-3 rounded-xl font-semibold transition-colors"
        >
          이 상품으로 가입하기
        </Link>
      </div>
    </main>
  );
}

function Section({ title, children }: { title: string; children: React.ReactNode }) {
  return (
    <div className="mb-6">
      <h2 className="text-base font-semibold mb-3 text-gray-800">{title}</h2>
      <div className="border rounded-lg overflow-hidden">{children}</div>
    </div>
  );
}


function RiderRow({ rider }: { rider: ProductRider }) {
  return (
    <div className="px-4 py-3 bg-white">
      <div className="flex items-center justify-between">
        <span className="text-sm font-medium">{rider.riderName}</span>
        {rider.isDefault && (
          <span className="text-xs bg-gray-100 text-gray-500 px-2 py-0.5 rounded">기본 포함</span>
        )}
      </div>
      {rider.description && (
        <p className="text-xs text-gray-400 mt-1 leading-relaxed">{rider.description}</p>
      )}
    </div>
  );
}

