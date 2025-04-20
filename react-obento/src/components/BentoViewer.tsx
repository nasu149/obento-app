// src/components/BentoViewer.tsx
import { useEffect, useState } from "react";
import { BentoRecord, Configuration, RecordsUserIdGetRequest } from "../api/bento-service";
import { BentoResourceApi } from "../api/bento-service";

const config = new Configuration({ basePath: "http://localhost:8081" });
const bentoApi = new BentoResourceApi(config);

type Props = {
  userId: string;
  date: string; // "2025-04-18" 形式
};

export const BentoViewer = ({ userId, date }: Props) => {
  const [bentoStatus, setBentoStatus] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchBento = async () => {
      setLoading(true);
      try {
        const recordGetReqParameter: RecordsUserIdGetRequest = {userId: "aaa"};
        bentoApi.recordsUserIdGet(recordGetReqParameter).then((data: BentoRecord[]) => {
          console.log(data);
          for (let index = 0; index < data.length; index++) {
            const element = data[index];
            console.log(element);
            setBentoStatus(element.who ?? "");
          }
        });
      } catch (err) {
        console.error(err);
        setError("取得に失敗しました");
      } finally {
        setLoading(false);
      }
    };

    fetchBento();
  }, [userId, date]);

  if (loading) return <p>読み込み中…</p>;
  if (error) return <p className="text-red-500">{error}</p>;

  return (
    <div className="p-4 border rounded-xl shadow-md">
      <p className="font-bold text-lg">📅 {date} のお弁当情報</p>
      <p>ユーザー: {userId}</p>
      <p>ステータス: {bentoStatus}</p>
    </div>
  );
};
