package com.eigenroute.portfolioanalysis.rebalancing

import com.eigenroute.portfolioanalysis.PortfolioFixture
import org.scalamock.scalatest.MockFactory
import org.scalatest._

class FirstEstimateQuantitiesToAcquireCalculatorUTest
  extends FlatSpec
  with ShouldMatchers
  with PortfolioFixture
  with MockFactory {

  trait Fixture {
    val mockValueDifferencesCalculator = mock[ValueDifferencesCalculator]
  }

  "The quantity difference calculator" should "calculate the new quantities to be purchased such that as much free cash as" +
  "possible is invested, with the minimum deviation from the desired weights" in new DesiredValueFixture with Fixture
  with EstimatedQuantitiesToAcquire {

    (mockValueDifferencesCalculator.valueDifferences _)
    .expects(portfolioDesign, portfolioSnapshot, 0d, 10d, 0d, 0d)
    .returning(expectedValueDifferenceAllTrades)
    checkFirstEstimateQuantitiesToAcquire(
      mockValueDifferencesCalculator, expectedFirstEstimateQuantitiesAllTrades, 0.0011, 0d, 10d, 0d, 0d)

    (mockValueDifferencesCalculator.valueDifferences _)
    .expects(portfolioDesign, portfolioSnapshot, 0d, 10d, 0d, 0d)
    .returning(expectedValueDifferenceAllTrades)
    checkFirstEstimateQuantitiesToAcquire(
      mockValueDifferencesCalculator, expectedFirstEstimateQuantitiesAllTradesExpensive, 0.0025, 0d, 10d, 0d, 0d)

    (mockValueDifferencesCalculator.valueDifferences _)
    .expects(portfolioDesign, portfolioSnapshot, 0d, 10d, 0d, 0d)
    .returning(expectedValueDifferenceOneTrade)
    checkFirstEstimateQuantitiesToAcquire(
      mockValueDifferencesCalculator, expectedFirstEstimateQuantitiesOneTrade, 0.0011, 0d, 10d, 0d, 0d)

    (mockValueDifferencesCalculator.valueDifferences _)
    .expects(portfolioDesign, portfolioSnapshot, 0d, 10d, 0d, 0d)
    .returning(expectedValueDifferenceNoTrades)
    checkFirstEstimateQuantitiesToAcquire(
      mockValueDifferencesCalculator, expectedFirstEstimateQuantitiesNoTrades, 0.0011, 0d, 10d, 0d, 0d)

  }

  it should "calculate the new quantities to acquire for the initial investment" in new DesiredValueFixture with Fixture
  with EstimatedQuantitiesToAcquire {

    (mockValueDifferencesCalculator.valueDifferences _)
    .expects(portfolioDesign, portfolioSnapshotZeroQuantity, 0d, 10d, 0d, 10040d)
    .returning(expectedValueDifferenceFirstTrades)
    new FirstEstimateQuantitiesToAcquireCalculator(mockValueDifferencesCalculator)
    .firstEstimateQuantitiesToAcquire(
      portfolioDesign, portfolioSnapshotZeroQuantity, 0.0011, 0d, 10d, 0d, 10040d)
    .map { est =>
      PortfolioQuantityToAcquire(
        est.eTFCode, est.quantityToAcquire, round(est.effectivePrice), round(est.fractionalQuantity)) } should
      contain theSameElementsAs expectedFirstEstimateQuantitiesFirstTrades

  }

  private def checkFirstEstimateQuantitiesToAcquire(
      valueDifferencesCalculator: ValueDifferencesCalculator,
      expectedFirstEstimateQuantities: Seq[PortfolioQuantityToAcquire],
      bidAskCost: Double,
      maxAllowedDeviation: Double,
      perETFTradingCost: Double,
      accumulatedExDividends: Double,
      accumulatedCash: Double) {

    new FirstEstimateQuantitiesToAcquireCalculator(valueDifferencesCalculator)
    .firstEstimateQuantitiesToAcquire(
        portfolioDesign,
        portfolioSnapshot,
        bidAskCost,
        maxAllowedDeviation,
        perETFTradingCost,
        accumulatedExDividends,
        accumulatedCash)
    .map { est =>
      PortfolioQuantityToAcquire(
        est.eTFCode, est.quantityToAcquire, round(est.effectivePrice), round(est.fractionalQuantity)) } should
    contain theSameElementsAs expectedFirstEstimateQuantities

  }

}
