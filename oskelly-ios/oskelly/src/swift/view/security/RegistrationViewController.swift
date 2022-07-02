//
//  RegistrationViewController.swift
//  oskelly
//
//  Created by Виталий Хлудеев on 28.04.17.
//  Copyright © 2017 Виталий Хлудеев. All rights reserved.
//

import UIKit
import SnapKit

class RegistrationViewController: RegistrationBaseController {

    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.register(RegistrationView.self, forCellReuseIdentifier: "Row")
    }
}
