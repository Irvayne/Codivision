<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<div class="modal fade" id="upload">
	
	<div class="modal-dialog">
		
		<div class="modal-content">
		
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"
					aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
				<h4 class="modal-title">
					<fmt:message key="repository.upload.dialog"/>
				</h4>
			</div>

			<form method="post"
				action="${linkTo[RepositoryController].upload(repository.id)}"
				enctype="multipart/form-data">

				<div class="modal-body">
					<div class="form-group">
						<label for="fileRepository"><fmt:message key ="repository.upload.file"/>:</label> <input
							class="filestyle" data-buttonText="Abrir" data-icon="false" type="file" id="fileRepository" name="fileRepository" required>
					</div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">
						<fmt:message key="close" />
					</button>
					<button type="submit" class="btn btn-primary">
						<fmt:message key="update" />
					</button>
				</div>

			</form>

		</div>
		
	</div>
	
</div>