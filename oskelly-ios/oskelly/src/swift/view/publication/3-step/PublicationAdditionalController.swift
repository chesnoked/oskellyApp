//
// Created by Виталий Хлудеев on 25.10.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class PublicationAdditionalController : UITableViewController {

    var draft: Draft!

    override func viewDidLoad() {
        super.viewDidLoad()
        draft = GlobalProvider.instance.draftProvider.getCurrent()
        navigationItem.title = "Дополнительно"
        navigationItem.rightBarButtonItem = UIBarButtonItem(title: "OK", style: UIBarButtonItemStyle.plain, target: self, action: #selector(self.okPressed))
        tableView.register(PublicationVintageView.self, forCellReuseIdentifier: "PublicationVintageView")
        tableView.register(PublicationTextFieldView.self, forCellReuseIdentifier: "PublicationTextFieldView")
        tableView.separatorStyle = .none
        tableView.allowsSelection = false
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 6
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        switch indexPath.row {
            case 0:
                let cell = tableView.dequeueReusableCell(withIdentifier: "PublicationVintageView", for: indexPath) as! PublicationVintageView
                return cell
            case 1:
                let cell = tableView.dequeueReusableCell(withIdentifier: "PublicationTextFieldView", for: indexPath) as! PublicationTextFieldView
                cell.render(name: "Модель", value: draft.model, numbersOnly: false) { d in
                    d.model = cell.getValue()
                }
                return cell
            case 2:
                let cell = tableView.dequeueReusableCell(withIdentifier: "PublicationTextFieldView", for: indexPath) as! PublicationTextFieldView
                cell.render(name: "Место приобретения", value: draft.origin, numbersOnly: false) { d in
                    d.origin = cell.getValue()
                }
                return cell
            case 3:
                let cell = tableView.dequeueReusableCell(withIdentifier: "PublicationTextFieldView", for: indexPath) as! PublicationTextFieldView
                cell.render(name: "Стоимость приобретения", value: draft.purchasePrice != nil ? String(draft.purchasePrice!) : nil, numbersOnly: true) { d in
                    if (cell.getValue() != nil && cell.getValue()! != "") {
                        d.purchasePrice = Float(cell.getValue()!)
                    }
                    else {
                        d.purchasePrice = nil
                    }
                }
                return cell
            case 4:
                let cell = tableView.dequeueReusableCell(withIdentifier: "PublicationTextFieldView", for: indexPath) as! PublicationTextFieldView
                cell.render(name: "Год приобретения", value: draft.purchaseYear != nil  ? String(draft.purchaseYear!) : "", numbersOnly: true) { d in
                    if (cell.getValue() != nil && cell.getValue()! != "") {
                        d.purchaseYear = Int(cell.getValue()!)
                    }
                    else {
                        d.purchaseYear = nil
                    }
                }
                return cell
            case 5:
                let cell = tableView.dequeueReusableCell(withIdentifier: "PublicationTextFieldView", for: indexPath) as! PublicationTextFieldView
                cell.render(name: "Серийный номер", value: draft.serialNumber, numbersOnly: false) { d in
                    d.serialNumber = cell.getValue()
                }
                return cell
            default:
                return tableView.dequeueReusableCell(withIdentifier: "PublicationTextFieldView", for: indexPath)
        }
    }

    override func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return BaseTextFieldView.height
    }
    func okPressed() {
        navigationController?.popViewController(animated: true)
    }
}