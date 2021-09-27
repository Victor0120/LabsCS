import os
import json
from tkinter import *
import tkinter as tk
from tkinter import filedialog
import re

def choose_file():
    root = tk.Tk()
    root.withdraw()
    root.file_name = filedialog.askopenfilename(initialdir=f"{os.getcwd()}", title='Select file', filetypes=(('Audit files', '*.audit'), ('all files', '*.*')))
    file = root.file_name.split('/')[-1:][0]
    root.destroy()
    return file

class Parser:
    @property
    def open_audit(self):
        data = open(f"{choose_file()}", "r").read()
        return data

    def get_item(self):
        text = self.open_audit.split('Rationale:')
        items = []
        for item in text:
            if 'solution' in item and 'WARNING' not in item:
                items.append(item)
        return items

    def get_items_info(self):
        items = []
        info = self.get_item()

        i = 0
        for segment in info:
            parsed_item = {}
            i = i + 1

            parsed_item.update(
                {
                    'id': i,
                    'type': parse_item('type', segment),
                    'description': parse_item('description', segment),
                    'info': parse_item('info', segment),
                    'solution': parse_item('solution', segment),
                    'reference': parse_item('reference', segment),
                    'see_also': parse_item('see_also', segment),
                    'cmd': parse_item('cmd', segment),
                    'expect': parse_item('expect', segment),
                    'severity': parse_item('severity', segment),
                    'impact': parse_item('Impact', segment),
                }
            )
            items.append(parsed_item)

        return items

    def push_items_to_json(self):
        with open(f'tmp.json', 'w') as file_open:
            json.dump(self.get_items_info(), file_open, indent=2)

def parse_item(el, segment):
    els = ['description', 'reference', 'see_also', 'cmd', 'expect', 'severity']
    el_value = 'none'
    if el in els:
        ch = re.findall(r'{}\s+\:\s\".+\"'.format(el), segment)
        el_value = change_item(ch, el)

    elif el == 'type':
        ch = re.findall(r'{}\s+\:\s\S+'.format(el), segment)
        el_value = change_item(ch, el)

    elif el == 'info':
        ch = re.findall(r'{}\s+\:\s+\"\D+'.format(el), segment)
        el_value = change_item(ch, el).strip()

    elif el == 'solution':
        ch = re.findall('solution(.*?)Impact', segment, re.DOTALL)
        if len(ch) > 0:
            el_value = ch[0].replace(' : "', '').strip()

    elif el == 'Impact':
        ch = re.findall(r'{}:\s+\n+\D+\"\n'.format(el), segment)
        if len(ch) > 0:
            el_value = ch[0].replace(el, '').replace(':', '').replace('"', '').strip()

    return el_value

def change_item(ch, el):
    el_value = 'none'
    if len(ch) > 0:
        el_value = ch[0].replace(el, '').replace('"', '').replace(': ', '').replace("\\\\", "\\").replace('\n\n', '').strip()
    return el_value