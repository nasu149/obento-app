import React, { useState } from "react";

const daysInMonth = (year: number, month: number) => new Date(year, month + 1, 0).getDate();
const getFirstDay = (year: number, month: number) => new Date(year, month, 1).getDay();

const icons = {
  self: "✅",
  mom: "👩‍🍳",
  buy: "🍱",
};

type Status = keyof typeof icons | null;

const Calendar = () => {
  const today = new Date();
  const [currentDate, setCurrentDate] = useState(new Date(today.getFullYear(), today.getMonth()));
  const [data, setData] = useState<Record<string, Status>>({
    // 例: "2025-04-01": "self"
  });

  const year = currentDate.getFullYear();
  const month = currentDate.getMonth();
  const days = daysInMonth(year, month);
  const firstDay = getFirstDay(year, month);

  const formatDateKey = (d: number) => `${year}-${(month + 1).toString().padStart(2, "0")}-${d.toString().padStart(2, "0")}`;

  const handleSetStatus = (dateKey: string) => {
    const newStatus: Status = prompt("self / mom / buy を入力してください（空欄で削除）") as Status;
    if (newStatus === "self" || newStatus === "mom" || newStatus === "buy") {
      setData({ ...data, [dateKey]: newStatus });
    } else {
      // 削除扱い
      const newData = { ...data };
      delete newData[dateKey];
      setData(newData);
    }
  };

  const goToPrevMonth = () => {
    const newDate = new Date(year, month - 1);
    setCurrentDate(newDate);
  };

  const goToNextMonth = () => {
    const newDate = new Date(year, month + 1);
    setCurrentDate(newDate);
  };

  return (
    <div className="p-6 max-w-4xl mx-auto">
      <h1 className="text-2xl font-bold mb-4">📅 お弁当記録カレンダー</h1>
      <div className="flex items-center justify-between mb-4">
        <button onClick={goToPrevMonth} className="text-lg px-3 py-1 bg-gray-200 rounded hover:bg-gray-300">
          ←
        </button>
        <h1 className="text-2xl font-bold">
          {year}年 {month + 1}月
        </h1>
        <button onClick={goToNextMonth} className="text-lg px-3 py-1 bg-gray-200 rounded hover:bg-gray-300">
          →
        </button>
      </div>

      <div className="grid grid-cols-7 gap-2">
        {/* 曜日ヘッダー */}
        {["日", "月", "火", "水", "木", "金", "土"].map((day) => (
          <div key={day} className="text-center font-bold text-sm text-gray-500">
            {day}
          </div>
        ))}

        {/* 空白（前月分） */}
        {Array.from({ length: firstDay }).map((_, i) => (
          <div key={"empty-" + i}></div>
        ))}

        {/* 日付セル */}
        {Array.from({ length: days }, (_, i) => {
          const day = i + 1;
          const dateKey = formatDateKey(day);
          const status = data[dateKey] ?? null;

          return (
            <div
              key={day}
              className={`border rounded-lg h-24 flex flex-col items-center justify-between p-2 cursor-pointer hover:bg-yellow-50 ${
                today.getFullYear() === year && today.getMonth() === month && today.getDate() === day
                  ? "bg-yellow-100"
                  : "bg-white"
              }`}
              onClick={() => handleSetStatus(dateKey)}
            >
              <div className="text-sm font-medium">{day}</div>
              <div className="text-2xl h-8 flex items-center justify-center">
                {status ? icons[status] : ""}
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};

export default Calendar;