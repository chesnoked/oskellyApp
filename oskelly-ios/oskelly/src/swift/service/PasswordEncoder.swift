//
// Created by Виталий Хлудеев on 07.05.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import CryptoSwift

class PasswordEncoder {

    func getHashedPassword(password: String, salt: String) -> String {
        let saltedPassword = password + "{" + salt + "}"
        return saltedPassword.sha512()
    }
}