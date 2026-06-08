'use client';
import type { PartyInput } from '@/types/enrollment';

interface PartyFormProps {
  value: PartyInput;
  onChange: (data: Partial<PartyInput>) => void;
  placeholder?: Partial<PartyInput>;
  readOnly?: boolean;
}

export default function PartyForm({ value, onChange, placeholder, readOnly }: PartyFormProps) {
  const handleSsnFront = (val: string) => {
    const digits = val.replace(/[^0-9]/g, '');
    const currentBack = value.ssn.split('-')[1] || '';
    onChange({ ssn: currentBack ? `${digits}-${currentBack}` : digits });
  };

  const handleSsnBack = (val: string) => {
    const digits = val.replace(/[^0-9]/g, '');
    const currentFront = value.ssn.split('-')[0] || '';
    onChange({ ssn: `${currentFront}-${digits}` });
  };

  const ssnFront = value.ssn.split('-')[0] || '';
  const ssnBack = value.ssn.split('-')[1] || '';

  return (
    <div className="flex flex-col gap-3">
      <div>
        <label className="block text-sm font-medium mb-1">이름</label>
        <input
          type="text"
          readOnly={readOnly}
          className={`w-full border rounded-lg p-3 text-sm ${readOnly ? 'bg-gray-50 text-gray-700' : ''}`}
          placeholder={placeholder?.name || '이름'}
          value={value.name}
          onChange={(e) => onChange({ name: e.target.value })}
        />
      </div>
      <div>
        <label className="block text-sm font-medium mb-1">주민등록번호</label>
        <div className="flex items-center gap-2">
          <input
            type="text"
            inputMode="numeric"
            readOnly={readOnly}
            maxLength={6}
            className={`w-full border rounded-lg p-3 text-sm ${readOnly ? 'bg-gray-50 text-gray-700' : ''}`}
            placeholder="앞 6자리"
            value={ssnFront}
            onChange={(e) => handleSsnFront(e.target.value)}
          />
          <span className="text-gray-400">-</span>
          <input
            type={readOnly ? "password" : "password"}
            inputMode="numeric"
            readOnly={readOnly}
            maxLength={7}
            className={`w-full border rounded-lg p-3 text-sm ${readOnly ? 'bg-gray-50 text-gray-700' : ''}`}
            placeholder="뒤 7자리"
            value={ssnBack}
            onChange={(e) => handleSsnBack(e.target.value)}
          />
        </div>
      </div>
      <div>
        <label className="block text-sm font-medium mb-1">휴대폰 번호</label>
        <input
          type="tel"
          readOnly={readOnly}
          className={`w-full border rounded-lg p-3 text-sm ${readOnly ? 'bg-gray-50 text-gray-700' : ''}`}
          placeholder={placeholder?.phone || '010-0000-0000'}
          value={value.phone}
          onChange={(e) => onChange({ phone: e.target.value })}
        />
      </div>
    </div>
  );
}
