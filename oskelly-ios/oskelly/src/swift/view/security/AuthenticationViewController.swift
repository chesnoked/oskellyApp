//
// Created by Виталий Хлудеев on 02.05.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit

class AuthenticationViewController : RegistrationBaseController {

    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.register(AuthenticationView.self, forCellReuseIdentifier: "Row")
    }
}
