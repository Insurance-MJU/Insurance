interface Props {
  title: string;
  description?: string;
}

export default function NotImplemented({ title, description }: Props) {
  return (
    <div className="max-w-3xl">
      <h1 className="text-xl font-bold text-gray-800 mb-1">{title}</h1>
      {description && <p className="text-sm text-gray-500 mb-6">{description}</p>}
      <div className="bg-gray-50 border border-dashed border-gray-300 rounded-xl p-12 text-center">
        <p className="text-3xl mb-3">🚧</p>
        <p className="text-gray-500 font-medium">준비 중인 기능입니다</p>
        <p className="text-xs text-gray-400 mt-1">현재 프로젝트 범위(시나리오)에 포함되지 않은 기능입니다.</p>
      </div>
    </div>
  );
}
