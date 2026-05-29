import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { getCoverages, getRiders } from "@/queries/master";
import type { ProductCatalogItem, ProductCatalogDetail } from '@/types/product';
import { fetchApi, getCookie } from "./api";

const BASE_URL = process.env.NEXT_PUBLIC_JAVA_SERVER_URL;

export async function getProducts() {
  return fetchApi("/products").then(r => r.data ?? r);
}

export async function getProduct(id: number) {
  return fetchApi(`/products/${id}`);
}

export async function createProduct(data: any) {
  return fetchApi("/products", {
    method: "POST",
    body: JSON.stringify(data),
  });
}

// Insurance 백엔드에 PUT /products/{id} 미구현 - approval 엔드포인트로 대체
export async function updateProduct(id: number, data: any) {
  return fetchApi(`/products/${id}/approval`, { method: "PUT" });
}

// Insurance 백엔드 status → 엔드포인트 매핑
export async function changeProductStatus(id: number | string, status: string) {
  const APPROVAL = ["KIDI_SUBMITTED", "DESIGNING"];           // /approval (토글)
  const RATE_VER = ["KIDI_CONFIRMED", "FSS_APPLIED", "FSS_APPROVED"]; // /rate-verification (순환)
  const SALE     = ["FILING", "FILED", "ON_SALE"];            // /sale (순환)
  const DISCON   = ["DISCONTINUED"];

  if (APPROVAL.includes(status)) return fetchApi(`/products/${id}/approval`,          { method: "PUT" });
  if (RATE_VER.includes(status)) return fetchApi(`/products/${id}/rate-verification`, { method: "PUT" });
  if (SALE.includes(status))     return fetchApi(`/products/${id}/sale`,              { method: "PUT" });
  if (DISCON.includes(status))   return fetchApi(`/products/${id}/sale`,              { method: "PUT" });
  return fetchApi(`/products/${id}/approval`, { method: "PUT" });
}

export async function deleteProduct(id: number) {
  // Insurance 백엔드에 미구현
  throw new Error("상품 삭제는 현재 지원되지 않습니다.");
}

export async function applyForApproval(id: number) {
  return fetchApi(`/products/${id}/approval`, { method: "PUT" });
}

export async function applyRateVerification(id: number) {
  return fetchApi(`/products/${id}/rate-verification`, { method: "PUT" });
}

export async function confirmSale(id: number) {
  return fetchApi(`/products/${id}/sale`, { method: "PUT" });
}

export async function estimatePremium(id: number, data: any) {
  return fetchApi(`/products/${id}/estimate`, {
    method: "POST",
    body: JSON.stringify(data),
  });
}

export async function premiumCalculation(id: number, data: any) {
  return fetchApi(`/products/${id}/premium-calculation`, {
    method: "POST",
    body: JSON.stringify(data),
  });
}

export async function addProductDocument(productId: number | string, formData: FormData) {
  const docType = formData.get("docType") as string;
  const title   = formData.get("title") as string;
  const note    = ((formData.get("summary") ?? formData.get("note")) as string) ?? "";
  const file    = formData.get("file") as File | null;

  let filename    = "";
  let fileContent = "";

  if (file && file.size > 0) {
    if (file.size > 10 * 1024 * 1024) {
      throw new Error("파일 크기는 10MB 이하여야 합니다.");
    }
    filename = file.name;
    const buffer = await file.arrayBuffer();
    const bytes  = new Uint8Array(buffer);
    let binary   = "";
    const CHUNK  = 8192;
    for (let i = 0; i < bytes.length; i += CHUNK) {
      binary += String.fromCharCode(...(bytes.subarray(i, i + CHUNK) as any));
    }
    fileContent = btoa(binary);
  }

  return fetchApi(`/products/${productId}/documents`, {
    method: "POST",
    body: JSON.stringify({ docType, title, note, filename, fileContent }),
  });
}

export async function deleteProductDocument(productId: number | string, docId: string) {
  return fetchApi(`/products/${productId}/documents/${docId}`, { method: "DELETE" });
}

export async function downloadProductDocument(productId: number | string, docId: string, filename: string) {
  const token = getCookie("access_token");
  const res = await fetch(`${BASE_URL}/products/${productId}/documents/${docId}/download`, {
    headers: token ? { Authorization: `Bearer ${token}` } : {},
  });
  if (!res.ok) throw new Error("다운로드 실패");
  const blob = await res.blob();
  const url  = URL.createObjectURL(blob);
  const a    = document.createElement("a");
  a.href     = url;
  a.download = filename || "document";
  a.click();
  URL.revokeObjectURL(url);
}

async function fetchOnSaleProducts(): Promise<ProductCatalogItem[]> {
  const res = await fetch(`${BASE_URL}/public/products`);
  if (!res.ok) throw new Error('상품 목록을 불러오지 못했습니다.');
  const json = await res.json();
  return json.data ?? json;
}

async function fetchOnSaleProduct(id: string): Promise<ProductCatalogDetail> {
  const res = await fetch(`${BASE_URL}/public/products/${id}`);
  if (!res.ok) throw new Error('상품 정보를 불러오지 못했습니다.');
  const json = await res.json();
  return json.data ?? json;
}

export function useOnSaleProducts() {
  return useQuery<ProductCatalogItem[]>({
    queryKey: ['products', 'catalog'],
    queryFn: fetchOnSaleProducts,
    staleTime: 5 * 60 * 1000,
  });
}

export function useOnSaleProduct(id: string) {
  return useQuery<ProductCatalogDetail>({
    queryKey: ['products', 'catalog', id],
    queryFn: () => fetchOnSaleProduct(id),
    enabled: !!id,
    staleTime: 5 * 60 * 1000,
  });
}

export function useProductEditData(productId: number) {
  return useQuery({
    queryKey: ["product-edit-data", productId],
    queryFn: async () => {
      if (!productId) throw new Error("Invalid Product ID");
      const [productRes, coverages, riders] = await Promise.all([
        getProduct(productId),
        getCoverages(),
        getRiders(),
      ]);
      return {
        product: productRes.data ?? productRes,
        coverages,
        riders,
      };
    },
    enabled: !!productId,
  });
}

export function useUpdateProduct() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: any }) => updateProduct(id, data),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ["product", variables.id] });
      queryClient.invalidateQueries({ queryKey: ["product-edit-data", variables.id] });
      queryClient.invalidateQueries({ queryKey: ['products'] });
    },
  });
}

export function useCreateProduct() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: any) => createProduct(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['products'] });
    },
  });
}

export function useMasterData() {
  return useQuery({
    queryKey: ['master-data'],
    queryFn: async () => {
      const [coverages, riders] = await Promise.all([getCoverages(), getRiders()]);
      return { coverages, riders };
    },
    staleTime: 10 * 60 * 1000,
  });
}
