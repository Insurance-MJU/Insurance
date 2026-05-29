'use client';
import Link from 'next/link';
import { useOnSaleProducts } from '@/queries/products';
import type { ProductCatalogItem } from '@/types/product';

export default function ProductsPage() {
  const { data: products, isLoading, error } = useOnSaleProducts();

  if (isLoading) {
    return (
      <main className="max-w-xl mx-auto p-4">
        <h1 className="text-2xl font-bold mb-6">추천 보험 상품</h1>
        <div className="flex flex-col gap-4">
          {[1, 2].map((i) => (
            <div key={i} className="border rounded-lg p-5 shadow-sm animate-pulse">
              <div className="h-5 bg-gray-200 rounded w-2/3 mb-3" />
              <div className="h-4 bg-gray-100 rounded w-1/2 mb-4" />
              <div className="h-10 bg-gray-100 rounded" />
            </div>
          ))}
        </div>
      </main>
    );
  }

  if (error || !products?.length) {
    return (
      <main className="max-w-xl mx-auto p-4">
        <h1 className="text-2xl font-bold mb-6">추천 보험 상품</h1>
        <div className="border rounded-lg p-8 text-center text-gray-400">
          <p className="text-lg mb-2">현재 판매 중인 상품이 없습니다.</p>
          <p className="text-sm">잠시 후 다시 확인해 주세요.</p>
        </div>
      </main>
    );
  }

  return (
    <main className="max-w-xl mx-auto p-4">
      <h1 className="text-2xl font-bold mb-6">추천 보험 상품</h1>
      <div className="flex flex-col gap-4">
        {products.map((product) => (
          <ProductCard key={product.id} product={product} />
        ))}
      </div>
    </main>
  );
}

function ProductCard({ product }: { product: ProductCatalogItem }) {
  return (
    <div className="border rounded-lg p-5 shadow-sm">
      <div className="flex items-start justify-between mb-1">
        <h2 className="text-lg font-semibold">{product.productName}</h2>
        <span className="text-xs bg-green-100 text-green-700 px-2 py-0.5 rounded-full font-medium">
          판매 중
        </span>
      </div>
      <p className="text-sm text-gray-400 mb-1">{product.lineOfBusinessDisplayName}</p>
      {product.description && (
        <p className="text-sm text-gray-500 mb-3">{product.description}</p>
      )}
      <div className="flex gap-2">
        <Link
          href={`/insurance/products/${product.id}`}
          className="flex-1 bg-gray-100 hover:bg-gray-200 p-2 text-center rounded text-sm font-medium transition-colors"
        >
          상품 자세히 보기
        </Link>
        <Link
          href={`/insurance/apply?prodId=${product.id}`}
          className="flex-1 bg-blue-500 hover:bg-blue-600 text-white p-2 text-center rounded text-sm font-medium transition-colors"
        >
          바로 가입
        </Link>
      </div>
    </div>
  );
}

