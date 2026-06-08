'use client';
import { useEnrollmentStore } from '@/store/enrollmentStore';
import { calcAgeFromBirthYear } from '@/utils/format';
import type { FamilyMember } from '@/types/enrollment';

import { FAMILY_RELATIONS } from '@/types/enrollmentConstants';

export default function FamilyMemberForm() {
  const { driverScope, familyMembers, setFamilyMembers } = useEnrollmentStore();

  const addMember = () => {
    const maxCount = driverScope === 'COUPLE' ? 1 : 10;
    if (familyMembers.length >= maxCount) return;
    const newMember: FamilyMember = {
      id: crypto.randomUUID(),
      birthYear: new Date().getFullYear() - 30,
      gender: 'M',
      relation: driverScope === 'COUPLE' ? 'SPOUSE' : 'CHILD',
    };
    setFamilyMembers([...familyMembers, newMember]);
  };

  const updateMember = (id: string, changes: Partial<FamilyMember>) => {
    setFamilyMembers(familyMembers.map((m) => (m.id === id ? { ...m, ...changes } : m)));
  };

  const removeMember = (id: string) => {
    setFamilyMembers(familyMembers.filter((m) => m.id !== id));
  };

  const needsFamilyInput = driverScope === 'COUPLE' || driverScope === 'FAMILY';
  if (!needsFamilyInput) return null;

  return (
    <div className="border rounded-xl p-4 flex flex-col gap-4">
      <div className="flex items-center justify-between">
        <p className="text-sm font-semibold">
          {driverScope === 'COUPLE' ? '배우자 정보' : '가족 구성원 정보'}
        </p>
        {(driverScope === 'FAMILY' || familyMembers.length === 0) && (
          <button
            onClick={addMember}
            className="text-xs text-blue-500 border border-blue-400 px-3 py-1 rounded-lg"
          >
            + 추가
          </button>
        )}
      </div>

      {familyMembers.length === 0 && (
        <p className="text-xs text-gray-400 text-center py-2">
          {driverScope === 'COUPLE' ? '배우자를 추가해 주세요.' : '가족 구성원을 추가해 주세요.'}
        </p>
      )}

      {familyMembers.map((member) => (
        <div key={member.id} className="bg-gray-50 rounded-lg p-3 flex flex-col gap-3">
          <div className="flex justify-between items-center">
            <span className="text-xs font-medium text-gray-600">
              {FAMILY_RELATIONS.find((r) => r.value === member.relation)?.label}
            </span>
            <button onClick={() => removeMember(member.id)} className="text-xs text-red-400">삭제</button>
          </div>

          <div className="grid grid-cols-2 gap-2">
            {/* 관계 */}
            {driverScope === 'FAMILY' && (
              <div>
                <label className="block text-xs text-gray-500 mb-1">관계</label>
                <select
                  className="w-full border rounded p-2 text-xs bg-white"
                  value={member.relation}
                  onChange={(e) => updateMember(member.id, { relation: e.target.value as FamilyMember['relation'] })}
                >
                  {FAMILY_RELATIONS.map((r) => <option key={r.value} value={r.value}>{r.label}</option>)}
                </select>
              </div>
            )}

            {/* 성별 */}
            <div>
              <label className="block text-xs text-gray-500 mb-1">성별</label>
              <div className="flex gap-1">
                {(['M', 'F'] as const).map((g) => (
                  <button
                    key={g}
                    onClick={() => updateMember(member.id, { gender: g })}
                    className={`flex-1 py-2 rounded text-xs font-medium border transition-colors ${
                      member.gender === g ? 'bg-blue-500 text-white border-blue-500' : 'border-gray-300 text-gray-600'
                    }`}
                  >
                    {g === 'M' ? '남' : '여'}
                  </button>
                ))}
              </div>
            </div>

            {/* 출생연도 */}
            <div>
              <label className="block text-xs text-gray-500 mb-1">출생연도</label>
              <input
                type="number"
                min={1940}
                max={new Date().getFullYear() - 16}
                className="w-full border rounded p-2 text-xs"
                value={member.birthYear}
                onChange={(e) => updateMember(member.id, { birthYear: parseInt(e.target.value) || member.birthYear })}
              />
            </div>
          </div>

          <p className="text-xs text-blue-600">
            만 {calcAgeFromBirthYear(member.birthYear)}세
          </p>
        </div>
      ))}
    </div>
  );
}
