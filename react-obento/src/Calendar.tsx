import { useState } from "react";
import Calendar from "react-calendar";
import "react-calendar/dist/Calendar.css";
import { motion, AnimatePresence } from "framer-motion";
import { Who } from "./api/bento-service";

// type Who = "self" | "mom" | "buy";

type MyRecord = {
  date: string; // e.g. "2025-04-18"
  who: Who;
  earned: number;
};

const statusText: Record<Who, string> = {
  self: "✅ 自分で作った（+400円）",
  mom: "👩‍🍳 母が作った（+0円）",
  buy: "🍱 購買で購入（-400円相当）",
};

const iconMap: Record<Who, string> = {
  self: "✅",
  mom: "👩‍🍳",
  buy: "🍱",
};

const mockRecords: MyRecord[] = [
  { date: "2025-04-18", who: "self", earned: 400 },
  { date: "2025-04-16", who: "mom", earned: 0 },
  { date: "2025-04-15", who: "buy", earned: -400 },
];

const formatDate = (date: Date) => date.toISOString().split("T")[0];

export default function BentoCalendar() {
  const [selectedDate, setSelectedDate] = useState<Date | null>(null);
  const [recordForDate, setRecordForDate] = useState<MyRecord | null>(null);
  const [isModalOpen, setIsModalOpen] = useState(false);

  const handleDateClick = (date: Date) => {
    setSelectedDate(date);
    const record = mockRecords.find((r) => r.date === formatDate(date));
    setRecordForDate(record ?? null);
    setIsModalOpen(true);
  };

  const handleChoice = (who: Who) => {
    if (!selectedDate) return;
    const date = formatDate(selectedDate);
    const earned = who === "self" ? 400 : who === "mom" ? 0 : -400;

    const newRecord: MyRecord = { date, who, earned };
    setRecordForDate(newRecord);
    // 🔄 API 送信は後で実装
    setIsModalOpen(false);
  };

  return (
    <div className="p-6 max-w-2xl mx-auto w-full">
      <h1 className="text-2xl font-bold mb-6 text-center">📅 お弁当カレンダー</h1>

      <div className="w-full mx-auto rounded-xl overflow-hidden shadow-md border border-gray-300 bg-white p-4 flex justify-center">
        <Calendar
          onClickDay={handleDateClick}
          tileContent={({ date }) => {
            const record = mockRecords.find((r) => r.date === formatDate(date));
            return (
              <div className="h-5 flex items-center justify-center mt-1">
                {record ? <span className="text-xs">{iconMap[record.who]}</span> : null}
              </div>
            );
          }}
          className="w-full max-w-md mx-auto"
          tileClassName="text-sm p-2"
        />
      </div>

      {/* モーダル */}
      <AnimatePresence>
        {isModalOpen && selectedDate && (
          <motion.div
            className="fixed inset-0 bg-black bg-opacity-40 flex justify-center items-center z-50"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
          >
            <motion.div
              className="bg-white rounded-xl p-6 w-[90%] max-w-md shadow-lg"
              initial={{ scale: 0.8, opacity: 0 }}
              animate={{ scale: 1, opacity: 1 }}
              exit={{ scale: 0.8, opacity: 0 }}
            >
              <h2 className="text-lg font-bold mb-4 text-center">
                {formatDate(selectedDate)} の記録
              </h2>

              {recordForDate ? (
                <>
                  <p className="mb-2">{statusText[recordForDate.who]}</p>
                  <p className="text-sm text-gray-500 mb-4">
                    選択を変更できます👇
                  </p>
                </>
              ) : (
                <p className="mb-4">まだ記録がありません。選択して記録しましょう！</p>
              )}

              <div className="flex flex-col gap-2">
                {(["self", "mom", "buy"] as Who[]).map((who) => (
                  <motion.button
                    key={who}
                    onClick={() => handleChoice(who)}
                    disabled={recordForDate?.who === who}
                    whileHover={{ scale: recordForDate?.who !== who ? 1.03 : 1 }}
                    className={`px-4 py-2 rounded-xl font-medium text-white transition-all ${
                      who === "self"
                        ? "bg-gradient-to-r from-green-400 to-green-600"
                        : who === "mom"
                        ? "bg-gradient-to-r from-blue-400 to-blue-600"
                        : "bg-gradient-to-r from-yellow-400 to-yellow-600"
                    } ${recordForDate?.who === who ? "opacity-50 cursor-not-allowed" : ""}`}
                  >
                    {statusText[who]}
                  </motion.button>
                ))}
              </div>

              <button
                onClick={() => setIsModalOpen(false)}
                className="text-sm text-gray-500 hover:underline mt-4 mx-auto block"
              >
                閉じる
              </button>
            </motion.div>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
}