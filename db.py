import sqlite3

def get_all_description_info():
    conn = sqlite3.connect('file.db')
    c = conn.cursor()
    c.execute('''SELECT description FROM audits;''')

    rows = c.fetchall()
    info = []
    for row in rows:
        if row[0]:
            info.append(row[0])

    conn.commit()
    conn.close()

    return info

def selected_item_to_dict(number):
    conn = sqlite3.connect('file.db')
    c = conn.cursor()
    c.execute(f'''SELECT * FROM audits;''')

    rows = c.fetchall()
    info = []
    for row in rows:
        if row[0] == str(number + 1):
            info = row

    item = {}
    item.update(
        {
            'id': row[10],
            'type': row[9],
            'description': row[8],
            'info': row[7],
            'solution': row[6],
            'reference': row[5],
            'see_also': row[4],
            'cmd': row[3],
            'expect': row[2],
            'severity': row[1],
            'impact': row[0],
        }
    )

    conn.commit()
    conn.close()

    return item


def searched_item_to_dict(search_value):
    conn = sqlite3.connect('file.db')
    c = conn.cursor()
    c.execute(f'''SELECT * FROM audits WHERE description LIKE '%{search_value}%';''')

    rows = c.fetchall()
    info = []
    for row in rows:
        item = {}
        item.update(
            {
                'id': row[10],
                'type': row[9],
                'description': row[8],
                'info': row[7],
                'solution': row[6],
                'reference': row[5],
                'see_also': row[4],
                'cmd': row[3],
                'expect': row[2],
                'severity': row[1],
                'impact': row[0],
            }
        )
        info.append(item)

    conn.commit()
    conn.close()

    return info