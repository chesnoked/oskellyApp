//
// Created by Виталий Хлудеев on 05.05.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import Fabric
import Crashlytics

class AccountManager {

    private let defaults = UserDefaults.standard
    private let apiRequester: ApiRequester

    init(apiRequester: ApiRequester) {
        self.apiRequester = apiRequester
    }

    func isUserLoggedIn() -> Bool {
        return defaults.object(forKey: "email") != nil
    }

    func setDeviceToken(deviceToken: String) {
        defaults.set(deviceToken, forKey: "deviceToken")
    }

    func sendDeviceToken() {
        if (defaults.object(forKey: "deviceToken") != nil) {
            apiRequester.saveDeviceToken(token: defaults.object(forKey: "deviceToken") as! String)
        }
    }

    func setupCredentials(email: String, password: String) {
        defaults.set(email, forKey: "email")
        defaults.set(password, forKey: "password")

        Fabric.with([Crashlytics.self])
        Crashlytics.sharedInstance().setUserEmail(email)
    }

    func eraseCredentials() {
        defaults.removeObject(forKey: "email")
        defaults.removeObject(forKey: "password")
    }

    func getEmail() -> String  {
        return defaults.object(forKey: "email") as! String
    }

    func getPassword() -> String {
        return defaults.object(forKey: "password") as! String
    }
}