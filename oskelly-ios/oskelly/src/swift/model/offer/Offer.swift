//
//  Offer.swift
//
//  Created by Виталий Хлудеев on 10.01.18
//  Copyright (c) . All rights reserved.
//

import Foundation
import SwiftyJSON

public final class Offer: NSCoding {

  // MARK: Declaration for string constants to be used to decode and also serialize.
  private struct SerializationKeys {
    static let negotiationControlText = "negotiationControlText"
    static let negotiatedPrice = "negotiatedPrice"
    static let offersHistory = "offersHistory"
    static let reasonIfNotNegotiable = "reasonIfNotNegotiable"
    static let allowsNegotiation = "allowsNegotiation"
  }

  // MARK: Properties
  public var negotiationControlText: String?
  public var negotiatedPrice: String?
  public var offersHistory: [OfferHistory]?
  public var reasonIfNotNegotiable: String?
  public var allowsNegotiation: Bool? = false

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
    negotiationControlText = json[SerializationKeys.negotiationControlText].string
    negotiatedPrice = json[SerializationKeys.negotiatedPrice].string
    if let items = json[SerializationKeys.offersHistory].array { offersHistory = items.map { OfferHistory(json: $0) } }
    reasonIfNotNegotiable = json[SerializationKeys.reasonIfNotNegotiable].string
    allowsNegotiation = json[SerializationKeys.allowsNegotiation].bool
  }

  /// Generates description of the object in the form of a NSDictionary.
  ///
  /// - returns: A Key value pair containing all valid values in the object.
  public func dictionaryRepresentation() -> [String: Any] {
    var dictionary: [String: Any] = [:]
    if let value = negotiationControlText { dictionary[SerializationKeys.negotiationControlText] = value }
    if let value = negotiatedPrice { dictionary[SerializationKeys.negotiatedPrice] = value }
    if let value = offersHistory { dictionary[SerializationKeys.offersHistory] = value.map { $0.dictionaryRepresentation() } }
    if let value = reasonIfNotNegotiable { dictionary[SerializationKeys.reasonIfNotNegotiable] = value }
    if let value = allowsNegotiation { dictionary[SerializationKeys.allowsNegotiation] = value }
    return dictionary
  }

  // MARK: NSCoding Protocol
  required public init(coder aDecoder: NSCoder) {
    self.negotiationControlText = aDecoder.decodeObject(forKey: SerializationKeys.negotiationControlText) as? String
    self.negotiatedPrice = aDecoder.decodeObject(forKey: SerializationKeys.negotiatedPrice) as? String
    self.offersHistory = aDecoder.decodeObject(forKey: SerializationKeys.offersHistory) as? [OfferHistory]
    self.reasonIfNotNegotiable = aDecoder.decodeObject(forKey: SerializationKeys.reasonIfNotNegotiable) as? String
    self.allowsNegotiation = aDecoder.decodeObject(forKey: SerializationKeys.allowsNegotiation) as? Bool
  }

  public func encode(with aCoder: NSCoder) {
    aCoder.encode(negotiationControlText, forKey: SerializationKeys.negotiationControlText)
    aCoder.encode(negotiatedPrice, forKey: SerializationKeys.negotiatedPrice)
    aCoder.encode(offersHistory, forKey: SerializationKeys.offersHistory)
    aCoder.encode(reasonIfNotNegotiable, forKey: SerializationKeys.reasonIfNotNegotiable)
    aCoder.encode(allowsNegotiation, forKey: SerializationKeys.allowsNegotiation)
  }

}
