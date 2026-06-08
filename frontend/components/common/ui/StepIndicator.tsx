import React from 'react';

interface StepIndicatorProps {
    steps: string[];
    currentStep: number;
}

export function StepIndicator({ steps, currentStep }: StepIndicatorProps) {
    return (
        <div className="flex items-center gap-2 mb-8">
            {steps.map((s, i) => (
                <div key={i} className="flex items-center gap-2">
                    <div className={`w-7 h-7 rounded-full flex items-center justify-center text-xs font-bold transition-colors
                            ${i === currentStep ? "bg-blue-600 text-white" : i < currentStep ? "bg-green-500 text-white" : "bg-gray-200 text-gray-500"}`}>
                        {i < currentStep ? "✓" : i + 1}
                    </div>
                    <span className={`text-sm font-medium ${i === currentStep ? "text-blue-600" : i < currentStep ? "text-green-600" : "text-gray-400"}`}>
                        {s}
                    </span>
                    {i < steps.length - 1 && <div className="w-8 h-px bg-gray-200" />}
                </div>
            ))}
        </div>
    );
}
