//
//  RegistrationBaseController.swift
//  oskelly
//
//  Created by Виталий Хлудеев on 12.06.17.
//  Copyright © 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class RegistrationBaseController: UITableViewController {

    let height = max(UIScreen.main.bounds.width, UIScreen.main.bounds.height)

    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.separatorStyle = .none
        tableView.allowsSelection = false
        tableView.register(UITableViewCell.self, forCellReuseIdentifier: "EmptyRow")

        NotificationCenter.default.addObserver(self, selector: #selector(keyboardShow), name:NSNotification.Name.UIKeyboardDidShow, object: nil)
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 2
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        switch indexPath.row {
            case 1:
                let cell = tableView.dequeueReusableCell(withIdentifier: "Row", for: indexPath) as! RegistrationBaseView
                cell.render(controller: self)
                return cell
            default:
                return tableView.dequeueReusableCell(withIdentifier: "EmptyRow", for: indexPath)
        }
    }

    override func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        switch indexPath.row {
            case 1:
                return height
            default:
                return 0
        }
    }

    func keyboardShow(notification:NSNotification){
        tableView.scrollRectToVisible(CGRect(x: 10, y: height - (height / 3.3), width: 100, height: 100), animated: true)
    }
}
