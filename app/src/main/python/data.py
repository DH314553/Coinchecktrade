import matplotlib.pyplot as plt
import pandas as pd
import tensorflow as tf

data = pd.read_csv("../data/coincheck-startDate-endDate.csv")
model = Prophet()
model.fit(data)
future_data = model.make_future_dataframe(periods=24, freq='H')
forecast_data = model.predict(future_data) #予測
data.plot()
model.plot(forecast_data)
plt.show()