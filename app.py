import tkinter as tk

from J_Z import *
from pps import Parser
from datetime import datetime
from db import get_all_description_info, searched_item_to_dict, selected_item_to_dict

class App(tk.Frame):
    def __init__(self, master=None):
        super().__init__(master)
        self.master = master
        self.pack()
        self.create_widgets()

    def create_widgets(self):
        self.quit_btn = tk.Button(self, text="QUIT", fg="Black", bg="red", command=self.master.destroy)
        self.entry = tk.Entry(self)
        self.search = tk.Label(self)
        self.search = tk.Label(self, text="Search:")
        self.export_selected = tk.Button(self, text='Export selected items to json',
                                         fg="Black", bg="green",
                                         command=self.searched_items_to_json)
        self.entry.pack(side="right")
        self.search.pack(side="right")
        self.quit_btn.pack(side = "left", padx=40,)
        self.export_selected.pack(side="left", padx=20)

        self._list = tk.Listbox(window, selectmode="multiple", bg='white')
        self._list.pack(side="top", ipadx = 450, ipady = 125)

        x = get_all_description_info()

        for each_item in range(len(x)):
            self._list.insert(tk.END, x[each_item])

    def selected_items_to_json(self):
        if len(self.return_all_items()) == 2 and self.return_all_items()[1] == '':
            now = datetime.now()
            with open(f'{now.strftime("%d_%m_%Y_%H_%M_%s")}.json', 'w') as file_open:
                json.dump(selected_item_to_dict(int(self.return_all_items()[0])), file_open, indent=2)
        else:
            items = []
            for item in self.return_all_items():
                items.append(selected_item_to_dict(int(item)))

            now = datetime.now()
            with open(f'{now.strftime("%d_%m_%Y_%H_%M_%s")}.json', 'w') as file_open:
                json.dump(items, file_open, indent=2)

    def result_message(self):
        now = datetime.now()
        result_window = tk.Tk()
        result_window.geometry("500x50")
        result_window['bg'] = 'white'
        result_window.resizable(0, 0)
        result_window.title('Info')
        result_label = tk.Label(result_window,
                                text=f"Audit was exported to "f"{now.strftime(f'%d_%m_%Y_%H_%M_{self.entry.get().strip()}')}.json",
                                bg='white')
        result_label.pack()
        close_btn = tk.Button(result_window, text='Close',
                              fg="white", bg="red",
                              command=result_window.destroy)
        close_btn.pack()

    def searched_items_to_json(self):
        items = []
        self.result_message()
        search = self.entry.get().strip()
        for item in searched_item_to_dict(self.entry.get().strip()):
            items.append(item)
        now = datetime.now()
        with open(f'{now.strftime(f"%d_%m_%Y_%H_%M_{search}")}.json', 'w') as file_open:
            json.dump(items, file_open, indent=2)
            self.entry.delete(0, 'end')

window = tk.Tk(className=' LabSEC')
window.geometry("900x500+50+50")
window['bg'] = 'white'
window.resizable(True, True)
Parser().push_items_to_json()
json_to_db()

app = App(master=window)
app['bg'] = 'white'
app.mainloop()


