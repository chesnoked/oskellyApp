//
// Created by Виталий Хлудеев on 12.02.18.
// Copyright (c) 2018 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class RowWithAction {

    let key: String
    let action: () -> ()

    init(key: String, action: @escaping () -> ()) {
        self.key = key
        self.action = action
    }
}