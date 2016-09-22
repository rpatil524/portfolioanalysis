package com.eigenroute.portfolioanalysis

import com.eigenroute.portfolioanalysis.rebalancing.{ETFCode, ETFData, ETFDataPlus}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession

object Main {

  def main(args: Array[String]): Unit = {

    val conf =
      new SparkConf()
      .setAppName("Simple Application").setMaster("local").set("spark.rpc.netty.dispatcher.numThreads","2")
    val sc = new SparkContext(conf)
    val spark = SparkSession.builder().appName("financial_data").master("local").getOrCreate()
    import spark.implicits._

    val ds = spark.read.format("jdbc").options(Config.dBParams).load.as[ETFData]

    val dsPlus = ds.map { eTFData =>
      ETFDataPlus(
       eTFData.asOfDate, ETFCode(eTFData.code), eTFData.xnumber, eTFData.nAV, eTFData.exDividend, 0, 0) }

    ds.show(false)

    sc.stop()
  }

}
