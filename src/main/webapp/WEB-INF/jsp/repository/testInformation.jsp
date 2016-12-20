<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<head>
<link
	href="<c:url value="/vendor/jstree/themes/default/style.min.css" />"
	rel="stylesheet">
</head>

<body>

	<div class="row">
		<div class="col-lg-12">
			<div class="panel panel-default">
				<div class="panel-body">
					<div class="row vdivided">
						<div class="container-tree col-md-4">
							<input style="margin-bottom:10px" type="text" class="form-control" id="jstree-search" placeholder="Pesquisar">
							<div id="jstree"></div>
						</div>
						<center><h4 >Contribuição no Projeto ${repository.name}</h4></center>
						<hr>
						<div class="col-md-4 text-center">
							<div id="chartColumn"></div>
							
						</div>
						<div class="col-md-4 text-center">
							<div id="chartColumnTest"></div>
							
						</div>
					</div>
					<hr>
				<div class="col-md-12 text-center">
							<div id="chart"></div>
							
				</div>
				<hr>
				<div id=corpo  class="col-md-12 text-center">
							
							
							
				</div>
				
				
			</div>
		</div>
	</div>
</div>
	<input type="hidden" id="repository" value="${repository.id}" />
	<input type="hidden" id="repositoryName" value="${repository.name}" />
	<input type="hidden" id="extractionPath" value="${extractionPath.id}" />
	
	<script src="https://code.highcharts.com/highcharts.js"></script>
	<script src="https://code.highcharts.com/modules/exporting.js"></script>
	

	<script src="<c:url value="/vendor/jstree/jstree.min.js" />"></script>
	<script src="<c:url value="/vendor/highcharts/highcharts.js" />"></script>
	<script
		src="<c:url value="/vendor/highcharts/modules/no-data-to-display.js" />"></script>
	<script
		src="<c:url value="/vendor/bootstrap-datepicker/js/bootstrap-datepicker.min.js" />"></script>
	<script
		src="<c:url value="/vendor/bootstrap-datepicker/locales/bootstrap-datepicker.pt-BR.min.js" />"></script>


	<script>
		$(document).ready(
				function() {

					Highcharts.setOptions({lang: {noData: "Não houveram alterações neste diretório/arquivo no período especificado"}});
					
					var repositoryName = $('#repositoryName').val();
					var repository = $('#repository').val();
					var path = $('#extractionPath').val();
					
					
					$.ajax({
						type : 'POST',
						url : '/codivision/repository/' + repository + '/authors',
						success : function(dataAuthor) {
							
							for(i = 0; i< dataAuthor.length; i++){
								var nome = dataAuthor[i];
								//onde o elemento será adicionado
						        pai = document.getElementById("corpo");
						        //definimos qual elemento queremos criar
						        elem = document.createElement("div");
						        //Definimos o texto do elemento.
						       	elem.setAttribute( "id","chartAuthor"+nome );
						        //adicionamos o elemento com o texto na div corpo
						        pai.appendChild(elem);
							}
							for(i = 0; i< dataAuthor.length; i++){
								
								var nome = dataAuthor[i];
								chartsPlot(nome);
								
							}
						}
					});
					
					function chartsPlot(nome){
						
						$.ajax({
							type : 'POST',
							url : '/codivision/repository/' + repository + '/historyAuthor',
							data : {'author' : nome},
							success : function(datas) {
								
								Highcharts.chart('chartAuthor'+nome, {
					        title: {
					            text: 'Histórico de Commits do Desenvolvedor '+nome,
					            x: -20 //center
					        },
					        subtitle: {
					            text: 'Relação entre os commits comuns e commits de teste',
					            x: -20
					        },
					        xAxis: {
					            categories: datas.dataCategories
					        },
					        yAxis: {
					            title: {
					                text: 'Quantidade de Linhas Alteradas'
					            },
					            plotLines: [{
					                value: 0,
					                width: 1,
					                color: '#808080'
					            }]
					        },
					        tooltip: {
					            valueSuffix: ''
					        },
					        legend: {
					       
					        },
					        series: datas.dataSeries
					    });
						
							}
						});
						}

					$.ajax({
						type : 'POST',
						url : '/codivision/repository/'+repository+'/path/'+path+'/tree',
						success : function(treeData) {

							$('#jstree').jstree(
									{
										'core' : {
											'data' : treeData
										},
										'types' : {
											"FOLDER" : {
												"valid_children" : [ "FOLDER",
														"FILE" ]
											},
											"FILE" : {
												"icon" : "jstree-file",
												"valid_children" : [],
											}
										},
										'plugins' : [ "types", "wholerow",
												"sort", "search" ]
									});

							request("/");

						}

					});
					
					$('#jstree').on("changed.jstree", function (e, data) {
					    var newPath = "/" + data.instance.get_path(data.selected[0], "/");
					    request(newPath);
					});
					

					
					
					function request(newPath){
						
						$.ajax({
							type : 'POST',
							url : '/codivision/repository/'+repository+'/path/'+path+'/alterationsQntLineTest',
							data : {'newPath' : newPath},
							success : function(data) {
								 Highcharts.chart('chartColumnTest', {
								        chart: {
								            type: 'column'
								        },
								        title: {
								            text: ''
								        },
								        subtitle: {
								            text: 'Contribuição dos desenvolvedores nos testes'
								        },
								        xAxis: {
								            categories: [
								                'Contribuição'
								            ],
								            crosshair: true
								        },
								        yAxis: {
								            min: 0,
								            title: {
								                text: 'Quantidade de Linhas'
								            }
								        },
								        tooltip: {
								            headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
								            pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
								                '<td style="padding:0"><b>{point.y:.1f} </b></td></tr>',
								            footerFormat: '</table>',
								            shared: true,
								            useHTML: true
								        },
								        plotOptions: {
								            column: {
								                pointPadding: 0.2,
								                borderWidth: 0
								            }
								        },
								        series: data
								    });
							}
						});
						
						
						$.ajax({
							type : 'POST',
							url : '/codivision/repository/'+repository+'/path/'+path+'/alterationsQntLine',
							data : {'newPath' : newPath},
							success : function(data) {
								 Highcharts.chart('chartColumn', {
								        chart: {
								            type: 'column'
								        },
								        title: {
								            text: ''
								        },
								        subtitle: {
								            text: 'Contribuição dos desenvolvedores'
								        },
								        xAxis: {
								            categories: [
								                'Contribuição'
								            ],
								            crosshair: true
								        },
								        yAxis: {
								            min: 0,
								            title: {
								                text: 'Quantidade de Linhas'
								            }
								        },
								        tooltip: {
								            headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
								            pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
								                '<td style="padding:0"><b>{point.y:.1f} </b></td></tr>',
								            footerFormat: '</table>',
								            shared: true,
								            useHTML: true
								        },
								        plotOptions: {
								            column: {
								                pointPadding: 0.2,
								                borderWidth: 0
								            }
								        },
								        series: data
								    });
							}
						});
						
					}
					
					
					
					
					$.ajax({
						type : 'POST',
						url : '/codivision/repository/' + repository + '/testInformation',
						success : function(data) {
					
					Highcharts.chart('chart', {
				        title: {
				            text: 'Histórico de Commits do Projeto '+repositoryName,
				            x: -20 //center
				        },
				        subtitle: {
				            text: 'Relação entre os commits comuns e commits de teste',
				            x: -20
				        },
				        xAxis: {
				            categories: data.dataCategories
				        },
				        yAxis: {
				            title: {
				                text: 'Quantidade de Linhas Alteradas'
				            },
				            plotLines: [{
				                value: 0,
				                width: 1,
				                color: '#808080'
				            }]
				        },
				        tooltip: {
				            valueSuffix: ''
				        },
				        legend: {
				       
				        },
				        series: data.dataSeries
				    });
					
						}
					});
					
					
					

				});
	</script>
</body>
