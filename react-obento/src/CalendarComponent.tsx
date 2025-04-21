import { useEffect, useState } from "react";
import Calendar from "react-calendar";
import "react-calendar/dist/Calendar.css";
import { motion, AnimatePresence } from "framer-motion";
import { BentoRecord, BentoResourceApi, Configuration, RecordsDatePutRequest, RecordsUserIdGetRequest, Who } from "./api/bento-service";


// type MyRecord = {
//   date: string; // e.g. "2025-04-18"
//   who: Who;
//   earned: number;
// };

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

// const mockRecords: MyRecord[] = [
//   { date: "2025-04-18", who: "self", earned: 400 },
//   { date: "2025-04-16", who: "mom", earned: 0 },
//   { date: "2025-04-15", who: "buy", earned: -400 },
// ];

// const formatDate = (date: Date) => date.toISOString().split("T")[0];
const formatDate = (date: Date) => date.toLocaleDateString('sv-SE');

type Props = {
  userId: string;
};

export function CalendarComponent({ userId }: Props) {
  const [selectedDate, setSelectedDate] = useState<Date | null>(null);
  const [recordForDate, setRecordForDate] = useState<BentoRecord | null>(null);
  const [isModalOpen, setIsModalOpen] = useState(false);


  const [recordsMap, setRecordMap] = useState<Map<string, BentoRecord>>();
  const config = new Configuration({ basePath: "http://localhost:8081" });
  const bentoApi = new BentoResourceApi(config);
  useEffect(() => {
    const recordGetReqParameter: RecordsUserIdGetRequest = {userId: userId};
    bentoApi.recordsUserIdGet(recordGetReqParameter).then((res) => {
      console.log("recordsUserIdGet res = ", res);
      const recordMap = res.reduce((map, record) => {
        if (record.date != undefined) 
          map.set(formatDate(record.date), record);
        return map;
      }, new Map<string, BentoRecord>());

      setRecordMap(recordMap);
    }).catch((error) => {
      console.log("myerror = ", error);
    });
  }, []);

  const handleDateClick = (date: Date) => {
    setSelectedDate(date);
    const record = recordsMap?.get(formatDate(date));
    setRecordForDate(record ?? null);
    setIsModalOpen(true);
  };

  const handleChoice = (who: Who) => {
    if (!selectedDate) return;
    const date = selectedDate;

    const newRecord: BentoRecord = { date: date, who: who, userId: userId };
    setRecordForDate(newRecord);

    if (newRecord.date != undefined) {
      recordsMap?.set(formatDate(newRecord.date), newRecord);
    }
    // 🔄 API 送信は後で実装
    fetchBento(newRecord);
    setIsModalOpen(false);
  };

  const fetchBento = async (bentoRecord: BentoRecord) => {
    try {
      // put request parameter create
      if (bentoRecord.date != undefined) {
        const bentoRequestParameter: RecordsDatePutRequest = {date: bentoRecord.date.toLocaleDateString('sv-SE'), bentoRecord: bentoRecord};
        // put request
        const res = await bentoApi.recordsDatePut(bentoRequestParameter);
        console.log("put request res = ", res);
      }
    } catch (err) {
      console.error(err);
    } finally {
      console.log("fetchBento end");
    }
  };

  return (
    <div className="p-6 max-w-2xl mx-auto w-full">
      <h1 className="text-2xl font-bold mb-6 text-center">📅 お弁当カレンダー</h1>

      <div className="w-full mx-auto rounded-xl overflow-hidden shadow-md border border-gray-300 bg-white p-4 flex justify-center">
        <Calendar
          onClickDay={handleDateClick}
          tileContent={({ date }) => {
            // console.log("titleContent, date = ", date);
            // console.log("titleContent, formatDate(date) = ", formatDate(date));
            const record = recordsMap?.get(formatDate(date));
            // console.log("titleContent, record = ", record);
            return (
              <div className="h-5 flex items-center justify-center mt-1">
                {record != undefined && record.who != undefined? <span className="text-xs">{iconMap[record.who]}</span> : null}
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

              {recordForDate && recordForDate.who != undefined ? (
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