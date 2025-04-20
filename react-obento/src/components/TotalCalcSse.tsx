import { useEffect, useState } from 'react'
import { TotalCalcPojo, TotalCalcPojoFromJSON } from '../api/total-calc-service';
import { Configuration, TotalCalcResourceApi, TotalUserIdGetRequest } from '../api/allowance-service';

type Props = {
    userId: string;
  };



export function TotalCalcSse({ userId }: Props) {

    const [pendingTotal, setPendingTotal] = useState<number>(0);
    const [selfRatio, setSelfRatio] = useState<number>(0.0);

    useEffect(() => {
        console.log("TotalClacSse start");
        const source = new EventSource(`http://localhost:8083/total/${userId}`);
    
        source.onmessage = (event) => {
            const totalCalc:TotalCalcPojo = TotalCalcPojoFromJSON(JSON.parse(event.data));
            console.log("totalCalcSse totalCalc = ", totalCalc);
            setTotalAndRatio(totalCalc);
        };
        return () => source.close();
    }, []);


    // リロード時に集計を取りに行く
    const config = new Configuration({ basePath: "http://localhost:8082" });
    const totalCalcResourceApi = new TotalCalcResourceApi(config);

    useEffect(() => {
        const totalUserIdGetRequest: TotalUserIdGetRequest = {userId: userId};
        totalCalcResourceApi.totalUserIdGet(totalUserIdGetRequest).then((res) => {
            console.log("res = ", res);
            setTotalAndRatio(res);
        });
    }, []);

    const setTotalAndRatio = (totalCalc: TotalCalcPojo) => {
        // total をセットする
        const total = totalCalc.totalAllowance;
        if (total == undefined) setPendingTotal(0);
        else setPendingTotal(total);

        // ratio をセットする
        const ratio = totalCalc.selfRatio;
        if (ratio == undefined) {
            setSelfRatio(0.0);
        } else {
            setSelfRatio(ratio*100);
        }
    };




    return (
        <div>
            {/* 今月の集計セクション */}
            <section className="bg-white rounded-2xl shadow-md border border-gray-200 p-6">
                <h2 className="text-lg font-bold text-gray-800 mb-3 flex items-center gap-2">
                📈 今月の集計
                </h2>
                <p className="text-gray-700 mb-1">💰 <span className="font-semibold">合計お小遣い：</span>{pendingTotal}円</p>
                <p className="text-gray-700">🍱 <span className="font-semibold">自炊率：</span>{selfRatio.toPrecision(3)}%</p>
            </section>
        </div>
    )
}
