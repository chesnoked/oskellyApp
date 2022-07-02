//
//  AccountOffer.swift
//
//  Created by Виталий Хлудеев on 09.02.18
//  Copyright (c) . All rights reserved.
//

import Foundation
import SwiftyJSON

public final class AccountOffer: NSCoding {

  // MARK: Declaration for string constants to be used to decode and also serialize.
  private struct SerializationKeys {
    static let productId = "productId"
    static let productName = "productName"
    static let image = "image"
    static let size = "size"
    static let waitingForConfirmation = "waitingForConfirmation"
    static let optionalFailMessage = "optionalFailMessage"
    static let history = "history"
    static let canBeAddedToCart = "canBeAddedToCart"
    static let waitingForConfirmationOfferId = "waitingForConfirmationOfferId"
    static let brand = "brand"
  }

  // MARK: Properties
  public var productId: Int?
  public var productName: String?
  public var image: String?
  public var size: String?
  public var waitingForConfirmation: Bool? = false
  public var optionalFailMessage: String?
  public var history: [AccountOfferHistory]?
  public var canBeAddedToCart: Bool? = false
  public var waitingForConfirmationOfferId: Int?
  public var brand: String?

  // MARK: SwiftyJSON Initializers
  /// Initiates the instance based on the object.
  ///
  /// - parameter object: The object of either Dictionary or Array kind that was passed.
  /// - returns: An initialized instance of the class.
  public convenience init(object: Any) {
    self.init(json: JSON(object))
  }

  /// Initiates the instance based on the JSON that was passed.
  ///
  /// - parameter json: JSON object from SwiftyJSON.
  public required init(json: JSON) {
    productId = json[SerializationKeys.productId].int
    productName = json[SerializationKeys.productName].string
    image = json[SerializationKeys.image].string
    size = json[SerializationKeys.size].string
    waitingForConfirmation = json[SerializationKeys.waitingForConfirmation].boolValue
    optionalFailMessage = json[SerializationKeys.optionalFailMessage].string
    if let items = json[SerializationKeys.history].array { history = items.map { AccountOfferHistory(json: $0) } }
    canBeAddedToCart = json[SerializationKeys.canBeAddedToCart].boolValue
    waitingForConfirmationOfferId = json[SerializationKeys.waitingForConfirmationOfferId].int
    brand = json[SerializationKeys.brand].string
  }

  /// Generates description of the object in the form of a NSDictionary.
  ///
  /// - returns: A Key value pair containing all valid values in the object.
  public func dictionaryRepresentation() -> [String: Any] {
    var dictionary: [String: Any] = [:]
    if let value = productId { dictionary[SerializationKeys.productId] = value }
    if let value = productName { dictionary[SerializationKeys.productName] = value }
    if let value = image { dictionary[SerializationKeys.image] = value }
    if let value = size { dictionary[SerializationKeys.size] = value }
    dictionary[SerializationKeys.waitingForConfirmation] = waitingForConfirmation
    if let value = optionalFailMessage { dictionary[SerializationKeys.optionalFailMessage] = value }
    if let value = history { dictionary[SerializationKeys.history] = value.map { $0.dictionaryRepresentation() } }
    dictionary[SerializationKeys.canBeAddedToCart] = canBeAddedToCart
    if let value = waitingForConfirmationOfferId { dictionary[SerializationKeys.waitingForConfirmationOfferId] = value }
    if let value = brand { dictionary[SerializationKeys.brand] = value }
    return dictionary
  }

  // MARK: NSCoding Protocol
  required public init(coder aDecoder: NSCoder) {
    self.productId = aDecoder.decodeObject(forKey: SerializationKeys.productId) as? Int
    self.productName = aDecoder.decodeObject(forKey: SerializationKeys.productName) as? String
    self.image = aDecoder.decodeObject(forKey: SerializationKeys.image) as? String
    self.size = aDecoder.decodeObject(forKey: SerializationKeys.size) as? String
    self.waitingForConfirmation = aDecoder.decodeBool(forKey: SerializationKeys.waitingForConfirmation)
    self.optionalFailMessage = aDecoder.decodeObject(forKey: SerializationKeys.optionalFailMessage) as? String
    self.history = aDecoder.decodeObject(forKey: SerializationKeys.history) as? [AccountOfferHistory]
    self.canBeAddedToCart = aDecoder.decodeBool(forKey: SerializationKeys.canBeAddedToCart)
    self.waitingForConfirmationOfferId = aDecoder.decodeObject(forKey: SerializationKeys.waitingForConfirmationOfferId) as? Int
    self.brand = aDecoder.decodeObject(forKey: SerializationKeys.brand) as? String
  }

  public func encode(with aCoder: NSCoder) {
    aCoder.encode(productId, forKey: SerializationKeys.productId)
    aCoder.encode(productName, forKey: SerializationKeys.productName)
    aCoder.encode(image, forKey: SerializationKeys.image)
    aCoder.encode(size, forKey: SerializationKeys.size)
    aCoder.encode(waitingForConfirmation, forKey: SerializationKeys.waitingForConfirmation)
    aCoder.encode(optionalFailMessage, forKey: SerializationKeys.optionalFailMessage)
    aCoder.encode(history, forKey: SerializationKeys.history)
    aCoder.encode(canBeAddedToCart, forKey: SerializationKeys.canBeAddedToCart)
    aCoder.encode(waitingForConfirmationOfferId, forKey: SerializationKeys.waitingForConfirmationOfferId)
    aCoder.encode(brand, forKey: SerializationKeys.brand)
  }

}
