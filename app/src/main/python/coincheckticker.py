import requests
import json
import datetime as dt
import pandas as pd
import numpy as np

startDate = dt.datetime.strptime(startDate, '%Y-%m-%d %H:%M:%S')
endDate = dt.datetime.strptime(endDate, '%Y-%m-%d %H:%M:%S')

startTimestamp = startDate.timestamp()
endTimestamp = endDate.timestamp()

query = {"periods": "3600", "after": str(int(startTimestamp)), "before": str(int(endTimestamp))}
res = json.loads(requests.get("https://coincheck.com/api/ticker", params=query).text)["result"]["3600"]
res = np.array(res)

time_stamp = res[:, 0].reshape(len(res), 1)
time_stamp = convertUnix2Date(time_stamp)
close_price = res[:, 4].reshape(len(res), 1)

tmp_data = np.hstack((time_stamp, close_price))
data = pd.DataFrame(tmp_data, columns={"y", "ds"})
data.to_csv("../data/coincheck-" + str(int(startTimestamp)) + "-" + str(int(endTimestamp)) + ".csv", index=False)