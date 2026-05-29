import React from 'react';

interface PhoneInputProps {
  value: string;
  onChange: (value: string) => void;
}

export function PhoneInput({ value, onChange }: PhoneInputProps) {
  const clean = value.replace(/-/g, '');
  const part1 = clean.slice(0, 3) || '010';
  const part2 = clean.slice(3, 7);
  const part3 = clean.slice(7, 11);

  const triggerChange = (p1: string, p2: string, p3: string) => {
    onChange(`${p1}${p2}${p3}`);
  };

  return (
    <div className="flex items-center gap-2">
      <select 
        value={part1} 
        onChange={e => triggerChange(e.target.value, part2, part3)}
        className="w-full border rounded-lg p-3 text-sm bg-white cursor-pointer"
      >
        <option value="010">010</option>
        <option value="011">011</option>
        <option value="016">016</option>
        <option value="017">017</option>
        <option value="018">018</option>
        <option value="019">019</option>
      </select>
      <span className="text-gray-400">-</span>
      <input
        type="text"
        inputMode="numeric"
        maxLength={4}
        value={part2}
        onChange={e => triggerChange(part1, e.target.value.replace(/\D/g, ''), part3)}
        className="w-full border rounded-lg p-3 text-sm"
        placeholder="1234"
      />
      <span className="text-gray-400">-</span>
      <input
        type="text"
        inputMode="numeric"
        maxLength={4}
        value={part3}
        onChange={e => triggerChange(part1, part2, e.target.value.replace(/\D/g, ''))}
        className="w-full border rounded-lg p-3 text-sm"
        placeholder="5678"
      />
    </div>
  );
}
