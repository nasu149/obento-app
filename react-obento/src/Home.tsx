// Home.tsx
import React, { useState } from "react";

const Home = () => {
  const [status, setStatus] = useState<string | null>(null);

  const handleRecord = (value: string) => {
    setStatus(value);
  };

  const statusText = {
    self: "✅ 自分で作った（+400円）",
    mom: "👩‍🍳 母が作った（+0円）",
    buy: "🍱 購買で購入（-400円相当）",
  };

  return (
    <div className="p-6 max-w-2xl mx-auto">
      <h1 className="text-2xl font-bold mb-6">🎒 お弁当記録ダッシュボード</h1>

      <section className="mb-6">
        <h2 className="text-lg font-semibold mb-2">今日の記録</h2>
        <div className="flex gap-3">
          <button
            onClick={() => handleRecord("self")}
            className="bg-green-500 text-white px-4 py-2 rounded-xl hover:bg-green-600"
          >
            ✅ 自分で作った
          </button>
          <button
            onClick={() => handleRecord("mom")}
            className="bg-blue-500 text-white px-4 py-2 rounded-xl hover:bg-blue-600"
          >
            👩‍🍳 母が作った
          </button>
          <button
            onClick={() => handleRecord("buy")}
            className="bg-yellow-500 text-white px-4 py-2 rounded-xl hover:bg-yellow-600"
          >
            🍱 購買で買った
          </button>
        </div>
      </section>

      <section className="mb-6">
        <h2 className="text-lg font-semibold mb-2">今日の結果</h2>
        <div className="p-4 bg-gray-100 rounded-lg">
          {status ? statusText[status as keyof typeof statusText] : "まだ記録されていません"}
        </div>
      </section>

      <section>
        <h2 className="text-lg font-semibold mb-2">今月の集計</h2>
        <div className="p-4 bg-gray-50 rounded-lg">
          <p>💰 合計お小遣い：4,800円</p>
          <p>📊 自炊率：80%</p>
        </div>
      </section>
    </div>
  );
};

export default Home;