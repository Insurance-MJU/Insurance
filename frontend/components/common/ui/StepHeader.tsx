import React from 'react';

export function StepHeader({ title, description }: { title: string; description?: string }) {
  return (
    <div>
      <h2 className="text-xl font-bold">{title}</h2>
      {description && <p className="text-sm text-gray-500 mt-1">{description}</p>}
    </div>
  );
}
