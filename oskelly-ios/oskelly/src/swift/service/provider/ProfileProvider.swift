//
// Created by Виталий Хлудеев on 10.11.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation

class ProfileProvider {

    private var profile: PublicProfile?

    private let apiRequester: ApiRequester

    init(apiRequester: ApiRequester) {
        self.apiRequester = apiRequester
    }

    func synchronizeProfile() {
        apiRequester.getCurrentProfile(completionHandler: {p in
            self.profile = p
        })
    }

    func getCurrentProfile(completionHandler: @escaping (PublicProfile) -> ()) {
        if let p = profile {
            completionHandler(p)
            self.synchronizeProfile()
        }
        else {
            apiRequester.getCurrentProfile(completionHandler: {p in
                self.profile = p
                completionHandler(p)
            })
        }
    }
}