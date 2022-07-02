//
// Created by Виталий Хлудеев on 20.10.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit

class DraftProvider {

    private let apiRequester: ApiRequester

    init(apiRequester: ApiRequester) {
        self.apiRequester = apiRequester
    }

    private var current: Draft!

    //TODO: метод updateCurrent()
    func setCurrent(draft: Draft) -> Draft {
        self.current = draft
        return self.current
    }

    func getCurrent() -> Draft {
        return self.current
    }

    func publish(draft: Draft, completionHandler: @escaping (Draft, String?) -> (), completePublication: Bool) {
        apiRequester.publish(draft: draft, completionHandler: {draft, error in
            completionHandler(draft, error)
            if(error == nil) {
                self.setCurrent(draft: draft)
            }
        }, completePublication: completePublication)
    }

    func publish(draft: Draft, completionHandler: @escaping (Draft, String?) -> ()) {
        publish(draft: draft, completionHandler: completionHandler, completePublication: false)
    }

    func uploadPhoto(image: UIImage, productId: Int, imageOrder: Int, completionHandler: @escaping (Draft) -> ()) {
        apiRequester.thirdStep(image: image, productId: productId, imageOrder: imageOrder, completionHandler: { draft in
            completionHandler(draft)
            self.setCurrent(draft: draft)
        })
    }
}