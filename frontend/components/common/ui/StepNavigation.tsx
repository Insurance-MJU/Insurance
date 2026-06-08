import React from 'react';

interface StepNavigationProps {
  onPrev?: () => void;
  onNext?: () => void;
  prevText?: string;
  nextText?: string;
  nextDisabled?: boolean;
  extraButton?: React.ReactNode;
}

export function StepNavigation({
  onPrev,
  onNext,
  prevText = '이전',
  nextText = '다음',
  nextDisabled = false,
  extraButton,
}: StepNavigationProps) {
  return (
    <div className="flex gap-3 mt-4">
      {extraButton}
      {onPrev && (
        <button
          onClick={onPrev}
          className="flex-1 border border-gray-300 text-gray-700 font-semibold p-4 rounded-xl transition-colors hover:bg-gray-50"
        >
          {prevText}
        </button>
      )}
      {onNext && (
        <button
          onClick={onNext}
          disabled={nextDisabled}
          className="flex-1 bg-blue-500 disabled:bg-gray-300 disabled:cursor-not-allowed text-white font-semibold p-4 rounded-xl transition-colors hover:bg-blue-600"
        >
          {nextText}
        </button>
      )}
    </div>
  );
}
