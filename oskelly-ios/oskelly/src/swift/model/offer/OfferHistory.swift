//
//  OfferHistory.swift
//
//  Created by Виталий Хлудеев on 10.01.18
//  Copyright (c) . All rights reserved.
//

import Foundation
import SwiftyJSON

public final class OfferHistory: NSCoding {

  // MARK: Declaration for string constants to be used to decode and also serialize.
  private struct SerializationKeys {
    static let price = "price"
    static let offeredAt = "zonedOfferedAt"
    static let acceptedMessage = "acceptedMessage"
    static let accepted = "accepted"
  }

  // MARK: Properties
  public var price: String?
  public var offeredAt: String?
  public var acceptedMessage: String?
  public var accepted: Bool?

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
    price = json[SerializationKeys.price].string
    offeredAt = json[SerializationKeys.offeredAt].string
    acceptedMessage = json[SerializationKeys.acceptedMessage].string
    accepted = json[SerializationKeys.accepted].bool
  }

  /// Generates description of the object in the form of a NSDictionary.
  ///
  /// - returns: A Key value pair containing all valid values in the object.
  public func dictionaryRepresentation() -> [String: Any] {
    var dictionary: [String: Any] = [:]
    if let value = price { dictionary[SerializationKeys.price] = value }
    if let value = offeredAt { dictionary[SerializationKeys.offeredAt] = value }
    if let value = acceptedMessage { dictionary[SerializationKeys.acceptedMessage] = value }
    if let value = accepted { dictionary[SerializationKeys.accepted] = value }
    return dictionary
  }

  // MARK: NSCoding Protocol
  required public init(coder aDecoder: NSCoder) {
    self.price = aDecoder.decodeObject(forKey: SerializationKeys.price) as? String
    self.offeredAt = aDecoder.decodeObject(forKey: SerializationKeys.offeredAt) as? String
    self.acceptedMessage = aDecoder.decodeObject(forKey: SerializationKeys.acceptedMessage) as? String
    self.accepted = aDecoder.decodeObject(forKey: SerializationKeys.accepted) as? Bool
  }

  public func encode(with aCoder: NSCoder) {
    aCoder.encode(price, forKey: SerializationKeys.price)
    aCoder.encode(offeredAt, forKey: SerializationKeys.offeredAt)
    aCoder.encode(acceptedMessage, forKey: SerializationKeys.acceptedMessage)
    aCoder.encode(accepted, forKey: SerializationKeys.accepted)
  }

}
