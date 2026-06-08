'use client';
import { useEnrollmentStore } from '@/store/enrollmentStore';
import { PhoneInput } from '@/components/forms/PhoneInput';

export default function Step01OwnerInfo() {
  const { ownerName, ownerSsnFront, ownerSsnBack, ownerPhone, setOwner, nextStep } =
    useEnrollmentStore();

  const isValid =
    ownerName.trim().length >= 2 &&
    ownerSsnFront.length === 6 &&
    ownerSsnBack.length === 7 &&
    ownerPhone.replace(/-/g, '').length >= 10;

  return (
    <div className="flex flex-col gap-6">
      <div>
        <h2 className="text-xl font-bold">소유자 정보 입력</h2>
        <p className="text-sm text-gray-500 mt-1">차량 소유자 본인의 정보를 입력해 주세요.</p>
      </div>

      <div className="flex flex-col gap-4">
        <div>
          <label className="block text-sm font-medium mb-1">이름</label>
          <input
            type="text"
            className="w-full border rounded-lg p-3 text-sm"
            placeholder="홍길동"
            value={ownerName}
            onChange={(e) => setOwner({ ownerName: e.target.value })}
          />
        </div>

        <div>
          <label className="block text-sm font-medium mb-1">주민등록번호</label>
          <div className="flex items-center gap-2">
            <input
              type="text"
              inputMode="numeric"
              maxLength={6}
              className="w-full border rounded-lg p-3 text-sm"
              placeholder="앞 6자리"
              value={ownerSsnFront}
              onChange={(e) => setOwner({ ownerSsnFront: e.target.value.replace(/\D/g, '') })}
            />
            <span className="text-gray-400">-</span>
            <input
              type="password"
              inputMode="numeric"
              maxLength={7}
              className="w-full border rounded-lg p-3 text-sm"
              placeholder="뒤 7자리"
              value={ownerSsnBack}
              onChange={(e) => setOwner({ ownerSsnBack: e.target.value.replace(/\D/g, '') })}
            />
          </div>
        </div>

        <div>
          <label className="block text-sm font-medium mb-1">휴대폰 번호</label>
          <PhoneInput
            value={ownerPhone}
            onChange={(val) => setOwner({ ownerPhone: val })}
          />
        </div>
      </div>

      <button
        disabled={!isValid}
        onClick={nextStep}
        className="w-full bg-blue-500 disabled:bg-gray-300 text-white font-semibold p-4 rounded-xl mt-2"
      >
        다음
      </button>
    </div>
  );
}
