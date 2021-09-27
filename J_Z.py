import pandas as pd
import json
import sqlite3

def json_to_db():
    with open('tmp.json', encoding='utf-8-sig') as f:
        data = json.load(f)

    df = pd.DataFrame(data)

    con = sqlite3.connect("file.db")
    c = con.cursor()
    df.to_sql("audits", con)