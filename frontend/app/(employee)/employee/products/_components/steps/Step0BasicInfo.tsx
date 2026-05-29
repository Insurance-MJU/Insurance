'use client';

import { ProductFormInfo, LOB_OPTIONS, STATUS_OPTIONS } from "@/types/product";

const I = "w-full border border-gray-200 rounded-md px-2.5 py-[7px] text-sm outline-none focus:border-blue-500 bg-white";
const L = "block text-xs font-medium text-gray-500 mb-1";

interface Props {
  info: ProductFormInfo;
  setInfo: (info: ProductFormInfo) => void;
}

export function Step0BasicInfo({ info, setInfo }: Props) {
  const update = (key: keyof ProductFormInfo, value: string) =>
    setInfo({ ...info, [key]: value });

  return (
    <div className="grid grid-cols-2 gap-4">
      <div>
        <label className={L}>상품코드 *</label>
        <input
          className={I}
          value={info.productCode}
          onChange={e => update("productCode", e.target.value)}
          placeholder="예: AUTO-2024-001"
        />
      </div>

      <div>
        <label className={L}>상품명 *</label>
        <input
          className={I}
          value={info.productName}
          onChange={e => update("productName", e.target.value)}
          placeholder="상품명을 입력하세요"
        />
      </div>

      <div>
        <label className={L}>보험종목</label>
        <select
          className={I}
          value={info.lineOfBusiness}
          onChange={e => update("lineOfBusiness", e.target.value)}
        >
          {LOB_OPTIONS.map(o => (
            <option key={o.value} value={o.value}>{o.label}</option>
          ))}
        </select>
      </div>

      <div>
        <label className={L}>가입대상</label>
        <input
          className={I}
          value={info.targetCustomer}
          onChange={e => update("targetCustomer", e.target.value)}
          placeholder="예: 만 18세 이상"
        />
      </div>

      <div>
        <label className={L}>판매시작일 *</label>
        <input
          type="date"
          className={I}
          value={info.saleStartDate}
          onChange={e => update("saleStartDate", e.target.value)}
        />
      </div>

      <div>
        <label className={L}>판매종료일</label>
        <input
          type="date"
          className={I}
          value={info.saleEndDate}
          onChange={e => update("saleEndDate", e.target.value)}
        />
      </div>

      <div>
        <label className={L}>상태</label>
        <select
          className={I}
          value={info.status}
          onChange={e => update("status", e.target.value)}
        >
          {STATUS_OPTIONS.map(o => (
            <option key={o.value} value={o.value}>{o.label}</option>
          ))}
        </select>
      </div>

      <div className="col-span-2">
        <label className={L}>상품 설명</label>
        <textarea
          className={`${I} resize-none`}
          rows={4}
          value={info.description}
          onChange={e => update("description", e.target.value)}
          placeholder="상품에 대한 설명을 입력하세요"
        />
      </div>
    </div>
  );
}
