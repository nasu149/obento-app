// Home.tsx
import { useEffect, useState } from "react";
import { BentoRecord, BentoResourceApi, Configuration, RecordsDatePutRequest, RecordsUserIdDateGetRequest, Who } from "./api/bento-service";
import { TotalCalcSse } from "./components/TotalCalcSse";

type Props = {
  userId: string;
};

const Home = ({ userId }: Props) => {
  const [displayText, setDisplayText] = useState<string>("まだ記録されていません");
  const [selectedStatus, setSelectedStatus] = useState<Who | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const config = new Configuration({ basePath: "http://localhost:8081" });
  const bentoApi = new BentoResourceApi(config);

  const statusText: Record<Who, string> = {
    self: "✅ 自分で作った（+400円）",
    mom: "👩‍🍳 母が作った（+0円）",
    buy: "🍱 購買で購入（-400円相当）",
  };

  useEffect(() => {
    const today = new Date();
    const recordGetReqParameter: RecordsUserIdDateGetRequest = {userId: userId, date: today.toLocaleDateString('sv-SE')};
    bentoApi.recordsUserIdDateGet(recordGetReqParameter).then((res) => {
      console.log("displayText res = ", res);
      if (res.who) {
        setSelectedStatus(res.who);
        setDisplayText(statusText[res.who]);
        console.log("get ok. displayText = ", statusText[res.who]);
      } 
    }).catch((error) => {
      console.log("myerror = ", error);
    });
  }, []);

  const handleRecord = (who: Who) => {
    // 選択済みだったら何もしない。
    if (isSubmitting || selectedStatus === who){
      console.log(who, " is already selected");
      return;
    } 

    fetchBento(who);

  };

  const fetchBento = async (who: Who) => {
    // setLoading(true);
    setDisplayText("Loading");
    setIsSubmitting(true); // 処理開始
    try {
      const today = new Date();
      const todayWho: Who = who;
      const bentoRecord: BentoRecord = {userId: userId, date: today, who: todayWho}
      console.log("today.toLocaleDateString()", today.toLocaleDateString('sv-SE'));

      // put request parameter create
      const bentoRequestParameter: RecordsDatePutRequest = {date: today.toLocaleDateString('sv-SE'), bentoRecord: bentoRecord};

      // put request
      const res = await bentoApi.recordsDatePut(bentoRequestParameter);
      console.log(res);

      setSelectedStatus(who);
      setDisplayText(statusText[who]);
    } catch (err) {
      console.error(err);
      setDisplayText("取得に失敗しました");
    } finally {
      setIsSubmitting(false); // 処理終了
      console.log("fetchBento end");
    }
  };

  const isDisabled = (who: Who) => isSubmitting || selectedStatus === who;

  return (
<div className="p-6 max-w-2xl mx-auto space-y-6">
  <h1 className="text-2xl font-bold mb-4 text-center">🎒 お弁当記録ダッシュボード</h1>

  {/* 今日の記録セクション */}
  <section className="bg-white rounded-2xl shadow-md border border-gray-200 p-6">
    <h2 className="text-lg font-bold text-gray-800 mb-4 flex items-center gap-2">
      🍱 今日の記録
    </h2>
    <div className="flex flex-col sm:flex-row justify-center gap-3">
      {/* 各ボタン共通クラスにしやすいようにまとめておく */}
      {[
        { type: Who.Self, label: "✅ 自分で作った", from: "from-green-400", to: "to-green-600", text: "text-green-800", bg: "bg-green-100", hover: "hover:bg-green-200" },
        { type: Who.Mom, label: "👩‍🍳 母が作った", from: "from-blue-400", to: "to-blue-600", text: "text-blue-800", bg: "bg-blue-100", hover: "hover:bg-blue-200" },
        { type: Who.Buy, label: "🍱 購買で買った", from: "from-yellow-400", to: "to-yellow-600", text: "text-yellow-800", bg: "bg-yellow-100", hover: "hover:bg-yellow-200" },
      ].map(({ type, label, from, to, text, bg, hover }) => (
        <button
          key={type}
          onClick={() => handleRecord(type)}
          disabled={isDisabled(type)}
          className={`sm:min-w-[180px] whitespace-nowrap px-4 py-2 rounded-xl font-medium shadow-sm text-sm sm:text-base text-center
            ${selectedStatus === type
              ? `bg-gradient-to-r ${from} ${to} text-white cursor-default`
              : `${bg} ${text} ${hover}`}`}
        >
          {label}
        </button>
      ))}
    </div>
  </section>

  {/* 今日の結果セクション */}
  <section className="bg-white rounded-2xl shadow-md border border-gray-200 p-6">
    <h2 className="text-lg font-bold text-gray-800 mb-3 flex items-center gap-2">
      📅 今日の結果
    </h2>
    <div className="text-gray-700 text-base">{displayText}</div>
  </section>

  {/* 今月の集計セクション */}
  <TotalCalcSse userId={userId} />

</div>

  );
};

export default Home;